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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import rs.ltt.jmap.annotation.Type;
import rs.ltt.jmap.common.entity.PushVerification;

public class TypeInjectionTest extends AbstractGsonTest {

    @Test
    public void serializeTypeWithDefaultName() {
        final PushVerification pushVerification = PushVerification.builder()
                .pushSubscriptionId("my-id")
                .verificationCode("very-secret")
                .build();
        final String json = getGson().toJson(pushVerification);
        Assertions.assertEquals(
                "{\"@type\":\"PushVerification\",\"pushSubscriptionId\":\"my-id\",\"verificationCode\":\"very-secret\"}",
                json
        );
    }

    @Test
    public void serializeTypeWithProvidedName() {
        final MyOwnType myOwnType = new MyOwnType();
        final String json = getGson().toJson(myOwnType);
        Assertions.assertEquals(
                "{\"@type\":\"aType\",\"foo\":\"bar\"}",
                json
        );
    }


    @Type("aType")
    public static class MyOwnType {
        private String foo = "bar";
    }

}
