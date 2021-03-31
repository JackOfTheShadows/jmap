/*
 * Copyright 2019 Daniel Gultsch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package rs.ltt.jmap.client;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonParseException;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import rs.ltt.jmap.client.api.EndpointNotFoundException;
import rs.ltt.jmap.client.api.MethodErrorResponseException;
import rs.ltt.jmap.client.api.MethodResponseNotFoundException;
import rs.ltt.jmap.client.event.CloseAfter;
import rs.ltt.jmap.client.session.Session;
import rs.ltt.jmap.common.entity.Email;
import rs.ltt.jmap.common.entity.Mailbox;
import rs.ltt.jmap.common.entity.capability.WebSocketCapability;
import rs.ltt.jmap.common.method.call.core.EchoMethodCall;
import rs.ltt.jmap.common.method.call.mailbox.GetMailboxMethodCall;
import rs.ltt.jmap.common.method.error.UnknownMethodMethodErrorResponse;
import rs.ltt.jmap.common.method.response.mailbox.GetMailboxMethodResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class HttpJmapClientTest {

    private static String ACCOUNT_ID = "test@example.com";
    private static String USERNAME = "test@example.com";
    private static String PASSWORD = "secret";
    public static String WELL_KNOWN_PATH = ".well-known/jmap";

    @Test
    public void fetchMailboxes() throws Exception {
        final MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(readResourceAsString("fetch-mailboxes/01-session.json")));
        server.enqueue(new MockResponse().setBody(readResourceAsString("fetch-mailboxes/02-mailboxes.json")));
        server.start();

        final JmapClient jmapClient = new JmapClient(
                USERNAME,
                PASSWORD,
                server.url(WELL_KNOWN_PATH)
        );


        final ListenableFuture<MethodResponses> future = jmapClient.call(
                GetMailboxMethodCall.builder().accountId(ACCOUNT_ID).build()
        );


        final GetMailboxMethodResponse mailboxResponse = future.get().getMain(GetMailboxMethodResponse.class);

        Assertions.assertEquals(7, mailboxResponse.getList().length);

        server.shutdown();
    }

    public static String readResourceAsString(String filename) throws IOException {
        return Resources.asCharSource(Resources.getResource(filename), Charsets.UTF_8).read().trim();
    }

    @Test
    public void fetchMailboxesWithMethodError() throws IOException, InterruptedException {
        final MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(readResourceAsString("fetch-mailboxes/01-session.json")));
        server.enqueue(new MockResponse().setBody(readResourceAsString("fetch-mailboxes/unknown-method.json")));
        server.start();

        final JmapClient jmapClient = new JmapClient(
                USERNAME,
                PASSWORD,
                server.url(WELL_KNOWN_PATH)
        );


        ListenableFuture<MethodResponses> future = jmapClient.call(
                GetMailboxMethodCall.builder().accountId(ACCOUNT_ID).build()
        );


        final ExecutionException exception = Assertions.assertThrows(ExecutionException.class, future::get);
        final Throwable cause = exception.getCause();
        MatcherAssert.assertThat(cause, CoreMatchers.instanceOf(MethodErrorResponseException.class));
        final MethodErrorResponseException methodErrorResponseException = (MethodErrorResponseException) cause;
        MatcherAssert.assertThat(methodErrorResponseException.getMethodErrorResponse(), CoreMatchers.instanceOf(UnknownMethodMethodErrorResponse.class));

        server.shutdown();
    }

    @Test
    public void fetchMailboxesException() throws IOException, InterruptedException, ExecutionException {
        final MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(readResourceAsString("fetch-mailboxes/01-session.json")));
        server.enqueue(new MockResponse().setBody(readResourceAsString("fetch-mailboxes/unknown-method-call-id.json")));
        server.start();

        final JmapClient jmapClient = new JmapClient(
                USERNAME,
                PASSWORD,
                server.url(WELL_KNOWN_PATH)
        );

        final ExecutionException exception = Assertions.assertThrows(ExecutionException.class, () -> {
            jmapClient.call(
                    GetMailboxMethodCall.builder().accountId(ACCOUNT_ID).build()
            ).get();
        });
        MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(MethodResponseNotFoundException.class));
        server.shutdown();
    }

    @Test
    public void endpointNotFound() throws IOException {
        final MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(404));

        final JmapClient jmapClient = new JmapClient(
                USERNAME,
                PASSWORD,
                server.url(WELL_KNOWN_PATH)
        );

        final ExecutionException exception = Assertions.assertThrows(
                ExecutionException.class,
                () -> jmapClient.call(EchoMethodCall.builder().libraryName(Version.getUserAgent()).build()).get()
        );

        MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(EndpointNotFoundException.class));

        server.shutdown();
    }

    @Test
    public void updateSessionResourceIfNecessary() throws IOException, InterruptedException, ExecutionException {
        final MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(readResourceAsString("update-session-resource/01-session.json")));
        server.enqueue(new MockResponse().setBody(readResourceAsString("update-session-resource/02-mailboxes.json")));
        server.enqueue(new MockResponse().setBody(readResourceAsString("update-session-resource/03-session.json")));
        server.enqueue(new MockResponse().setBody(readResourceAsString("update-session-resource/04-echo.json")));
        server.start();

        final JmapClient jmapClient = new JmapClient(
                USERNAME,
                PASSWORD,
                server.url(WELL_KNOWN_PATH)
        );

        final ListenableFuture<MethodResponses> mailboxFuture = jmapClient.call(
                GetMailboxMethodCall.builder().accountId(ACCOUNT_ID).build()
        );

        // Wait for result
        mailboxFuture.get();

        // Skip session request
        server.takeRequest();

        Assertions.assertEquals(server.url("/jmap/"), server.takeRequest().getRequestUrl());


        final ListenableFuture<MethodResponses> echoFuture = jmapClient.call(EchoMethodCall.builder().build());

        // Wait for result
        echoFuture.get();

        // Skip session request
        server.takeRequest();

        Assertions.assertEquals(server.url("/api/jmap/"), server.takeRequest().getRequestUrl());


        server.shutdown();
    }

    @Test
    public void redirectFromWellKnown() throws IOException, ExecutionException, InterruptedException {
        final MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(301).addHeader("Location", "/jmap"));
        server.enqueue(new MockResponse().setResponseCode(301).addHeader("Location", "/jmap/"));
        server.enqueue(new MockResponse().setBody(readResourceAsString("redirect/01-session.json")));

        server.start();

        final JmapClient jmapClient = new JmapClient(
                USERNAME,
                PASSWORD,
                server.url(WELL_KNOWN_PATH)
        );

        Session session = jmapClient.getSession().get();

        Assertions.assertEquals(server.url("/jmap/"), session.getBase());

        server.shutdown();

    }

    @Test
    public void downloadUploadAndEventSourceUrlTest() throws IOException, ExecutionException, InterruptedException {
        final MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(readResourceAsString("session-urls/01-session.json")));
        server.start();
        final JmapClient jmapClient = new JmapClient(
                USERNAME,
                PASSWORD,
                server.url(WELL_KNOWN_PATH)
        );

        final Session session = jmapClient.getSession().get();

        HttpUrl download = session.getDownloadUrl(USERNAME, "B10B1D", "lttrs", "text/plain");
        HttpUrl upload = session.getUploadUrl(USERNAME);
        HttpUrl eventSource = session.getEventSourceUrl(Arrays.asList(Email.class, Mailbox.class), CloseAfter.STATE, 300L);

        Assertions.assertEquals(server.url("/jmap/download/test%40example.com/B10B1D/lttrs?accept=text%2Fplain"), download);
        Assertions.assertEquals(server.url("/jmap/upload/test%40example.com/"), upload);
        Assertions.assertEquals(server.url("jmap/eventsource/?types=Email,Mailbox&closeafter=state&ping=300"), eventSource);

        server.shutdown();

    }

    @Test
    public void incompleteSessionResource() throws IOException, ExecutionException, InterruptedException {
        final MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(readResourceAsString("broken-session-urls/01-session.json")));
        server.start();
        final JmapClient jmapClient = new JmapClient(
                USERNAME,
                PASSWORD,
                server.url(WELL_KNOWN_PATH)
        );

        final Session session = jmapClient.getSession().get();
        Assertions.assertThrows(IllegalStateException.class, ()-> session.getUploadUrl(USERNAME));
        server.shutdown();
    }

    @Test
    public void webSocketUrl() throws IOException, ExecutionException, InterruptedException {
        final MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(readResourceAsString("session-urls/01-session.json")));
        server.start();
        final JmapClient jmapClient = new JmapClient(
                USERNAME,
                PASSWORD,
                server.url(WELL_KNOWN_PATH)
        );

        final Session session = jmapClient.getSession().get();
        Assertions.assertNotNull(session.getCapability(WebSocketCapability.class).getUrl());
    }

    @Test
    public void invalidJsonResponse() throws IOException {
        final MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(readResourceAsString("session-urls/01-session.json")));
        server.enqueue(new MockResponse().setBody("Garbage"));
        server.start();
        final JmapClient jmapClient = new JmapClient(
                USERNAME,
                PASSWORD,
                server.url(WELL_KNOWN_PATH)
        );
        final ListenableFuture<MethodResponses> future = jmapClient.call(
                GetMailboxMethodCall.builder().accountId(ACCOUNT_ID).build()
        );

        final ExecutionException executionException = Assertions.assertThrows(
                ExecutionException.class,
                ()-> future.get().getMain(GetMailboxMethodResponse.class)
        );

        MatcherAssert.assertThat(executionException.getCause(), CoreMatchers.instanceOf(JsonParseException.class));

        server.shutdown();

    }
}
