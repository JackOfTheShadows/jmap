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

package rs.ltt.jmap.client.api;


import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rs.ltt.jmap.client.http.BasicAuthHttpAuthentication;
import rs.ltt.jmap.client.http.HttpAuthentication;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;

public class HttpJmapApiClient extends AbstractJmapApiClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpJmapApiClient.class);

    private static final MediaType MEDIA_TYPE_JSON = MediaType.get("application/json");

    public static final OkHttpClient OK_HTTP_CLIENT;

    static {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        final Logger OK_HTTP_LOGGER = LoggerFactory.getLogger(OkHttpClient.class);
        builder.addInterceptor(new UserAgentInterceptor());
        if (OK_HTTP_LOGGER.isInfoEnabled()) {
            final HttpLoggingInterceptor loggingInterceptor;
            if (OK_HTTP_LOGGER.isDebugEnabled()) {
                loggingInterceptor = new HttpLoggingInterceptor(OK_HTTP_LOGGER::debug);
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            } else {
                loggingInterceptor = new HttpLoggingInterceptor(OK_HTTP_LOGGER::info);
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            }
            builder.addInterceptor(loggingInterceptor);
        }
        OK_HTTP_CLIENT = builder.build();
    }

    private final HttpUrl apiUrl;
    private final HttpAuthentication httpAuthentication;
    private final SessionStateListener sessionStateListener;

    public HttpJmapApiClient(final HttpUrl apiUrl, String username, String password) {
        this(apiUrl, new BasicAuthHttpAuthentication(username, password), null);
    }

    public HttpJmapApiClient(final HttpUrl apiUrl, final HttpAuthentication httpAuthentication) {
        this(apiUrl,httpAuthentication, null);
    }

    public HttpJmapApiClient(final HttpUrl apiUrl, final HttpAuthentication httpAuthentication, @Nullable final SessionStateListener sessionStateListener) {
        this.apiUrl = apiUrl;
        this.httpAuthentication = httpAuthentication;
        this.sessionStateListener = sessionStateListener;
    }

    @Override
    void onSessionStateRetrieved(final String sessionState) {
        LOGGER.debug("Notified of session state='{}'", sessionState);
        if (sessionStateListener != null) {
            sessionStateListener.onSessionStateRetrieved(sessionState);
        }
    }

    @Override
    ListenableFuture<InputStream> send(final String out) {
        final SettableFuture<InputStream> settableInputStreamFuture = SettableFuture.create();
        final Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(apiUrl);
        this.httpAuthentication.authenticate(requestBuilder);
        requestBuilder.post(RequestBody.create(out, MEDIA_TYPE_JSON));
        OK_HTTP_CLIENT.newCall(requestBuilder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@Nonnull Call call, @Nonnull IOException e) {
                settableInputStreamFuture.setException(e);
            }

            @Override
            public void onResponse(@Nonnull Call call, @Nonnull Response response) throws IOException {
                final int code = response.code();
                if (code == 404) {
                    settableInputStreamFuture.setException(
                            new EndpointNotFoundException(String.format("API URL(%s) not found", apiUrl))
                    );
                    return;
                }
                if (code == 401) {
                    settableInputStreamFuture.setException(
                            new UnauthorizedException(String.format("API URL(%s) was unauthorized", apiUrl))
                    );
                    return;
                }

                //TODO: code 500+ should probably just throw internal server error exception
                final ResponseBody body = response.body();
                if (body == null) {
                    settableInputStreamFuture.setException(
                            new IllegalStateException("response body was empty")
                    );
                    return;
                }
                settableInputStreamFuture.set(body.byteStream());
            }
        });
        return settableInputStreamFuture;
    }
}
