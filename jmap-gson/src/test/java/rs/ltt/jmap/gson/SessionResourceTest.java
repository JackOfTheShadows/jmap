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

package rs.ltt.jmap.gson;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.junit.Test;
import rs.ltt.jmap.common.SessionResource;
import rs.ltt.jmap.common.entity.Account;
import rs.ltt.jmap.common.entity.AccountCapability;
import rs.ltt.jmap.common.entity.Capability;
import rs.ltt.jmap.common.entity.capability.*;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;

public class SessionResourceTest extends AbstractGsonTest {


    @Test
    public void deserializeBasicRfcExample() throws IOException {
        final SessionResource session = parseFromResource("rfc-example/session.json", SessionResource.class);
        assertNotNull(session.getCapability(MailCapability.class));
        assertNotNull(session.getCapability(ContactsCapability.class));
        assertNull(session.getCapability(VacationResponseCapability.class));

        CoreCapability coreCapability = session.getCapability(CoreCapability.class);
        assertNotNull(coreCapability);
        assertEquals(Long.valueOf(50000000), coreCapability.getMaxSizeUpload());


        Map<String, Account> accounts = session.getAccounts();
        assertNotNull(accounts);
        assertEquals(2, session.getAccounts().size());

        Account account = accounts.get("A13824");
        assertNotNull(account);
        assertEquals("john@example.com", account.getName());

        assertNotNull(account.getCapability(MailAccountCapability.class));
        assertNull(account.getCapability(SubmissionAccountCapability.class));

    }

    @Test
    public void serialization() throws IOException {
        Map<Class<? extends Capability>, Capability> caps = new ImmutableMap.Builder<Class<? extends Capability>, Capability>()
                .put(MailCapability.class, MailCapability.builder().build())
                .put(CoreCapability.class, CoreCapability.builder()
                        .maxSizeUpload(5000L)
                        .maxCallsInRequest(2L)
                        .build()
                )
                .build();
        Map<Class<? extends AccountCapability>, AccountCapability> accountCaps = new ImmutableMap.Builder<Class<? extends AccountCapability>, AccountCapability>()
                .put(MailAccountCapability.class, MailAccountCapability.builder().build())
                .build();
        SessionResource resource = SessionResource.builder()
                .apiUrl("/jmap/")
                .capabilities(caps)
                .account("foo@example.com", Account.builder()
                        .accountCapabilities(accountCaps)
                        .build()
                )
                .build();
        Gson gson = getGson();
        assertEquals(readResourceAsString("session/basic.json"), gson.toJson(resource));
    }
}
