/*
 * Copyright 2021 Daniel Gultsch
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

package rs.ltt.jmap.gson;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import rs.ltt.jmap.common.Request;
import rs.ltt.jmap.common.method.call.email.SetEmailMethodCall;
import rs.ltt.jmap.common.util.Patches;
import rs.ltt.jmap.common.websocket.RequestWebSocketMessage;
import rs.ltt.jmap.common.websocket.ResponseWebSocketMessage;
import rs.ltt.jmap.common.websocket.WebSocketMessage;

import java.io.IOException;

public class WebSocketMessageTest extends AbstractGsonTest {

    @Test
    public void serializeRequest() throws IOException {
        final Request request = new Request.Builder().call(
                SetEmailMethodCall.builder()
                        .accountId("accountId")
                        .ifInState("state")
                        .update(ImmutableMap.of("M123", Patches.remove("keywords/$seen")))
                        .build()
        ).build();
        RequestWebSocketMessage message = RequestWebSocketMessage.builder()
                .requestId("my-id")
                .request(request)
                .build();
        Assertions.assertEquals(readResourceAsString("websocket/request.json"), getGson().toJson(message));
    }

    @Test
    public void deserializeResponse() throws IOException {
        final WebSocketMessage webSocketMessage = parseFromResource("websocket/response.json", WebSocketMessage.class);
        Assertions.assertTrue(webSocketMessage instanceof ResponseWebSocketMessage);
        final ResponseWebSocketMessage responseWebSocketMessage = (ResponseWebSocketMessage) webSocketMessage;
        Assertions.assertEquals(2, responseWebSocketMessage.getResponse().getMethodResponses().length);
    }

}
