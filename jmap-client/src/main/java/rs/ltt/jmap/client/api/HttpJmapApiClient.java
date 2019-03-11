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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rs.ltt.jmap.client.http.BasicAuthHttpAuthentication;
import rs.ltt.jmap.client.http.HttpAuthentication;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpJmapApiClient extends AbstractJmapApiClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpJmapApiClient.class);

    private final URL apiUrl;
    private final HttpAuthentication httpAuthentication;

    public HttpJmapApiClient(final URL apiUrl, String username, String password) {
        this(apiUrl, new BasicAuthHttpAuthentication(username, password));
    }

    public HttpJmapApiClient(final URL apiUrl, final HttpAuthentication httpAuthentication) {
        this.apiUrl = apiUrl;
        this.httpAuthentication = httpAuthentication;
    }

    @Override
    void onSessionStateRetrieved(String sessionState) {
        //System.out.println("sessionState="+sessionState);
    }

    @Override
    InputStream send(String out) throws IOException, JmapApiException {
        LOGGER.info(out);
        final HttpURLConnection connection = (HttpURLConnection) this.apiUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        this.httpAuthentication.authenticate(connection);
        connection.setDoOutput(true);
        OutputStream os = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
        writer.write(out);
        writer.flush();
        writer.close();
        os.close();
        final int code = connection.getResponseCode();
        LOGGER.debug(connection.getURL().toString()+" returned code="+code);
        if (code == 404) {
            throw new EndpointNotFoundException(String.format("API URL(%s) not found", connection.getURL()));
        }
        if (code == 401) {
            throw new UnauthorizedException(String.format("API URL(%s) was unauthorized", apiUrl));
        }
        //TODO: code 500+ should probably just throw internal server error exception
        return code >= 200 && code < 300 ? connection.getInputStream() : connection.getErrorStream();
    }
}
