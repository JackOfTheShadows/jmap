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

package rs.ltt.jmap.mua.util;

import com.google.common.base.CaseFormat;
import rs.ltt.jmap.common.entity.IdentifiableEmailWithMailboxIds;
import rs.ltt.jmap.common.entity.IdentifiableMailboxWithRole;
import rs.ltt.jmap.common.entity.Mailbox;
import rs.ltt.jmap.common.entity.Role;

import javax.annotation.Nullable;
import java.util.Collection;

public class MailboxUtil {

    public static @Nullable
    IdentifiableMailboxWithRole find(Collection<? extends IdentifiableMailboxWithRole> mailboxes, Role role) {
        for (IdentifiableMailboxWithRole mailbox : mailboxes) {
            if (mailbox.getRole() == role) {
                return mailbox;
            }
        }
        return null;
    }

    public static Mailbox create(final Role role) {
        return Mailbox.builder().role(role).name(humanReadable(role)).build();
    }

    public static String humanReadable(final Role role) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, role.toString());
    }

    public static boolean anyIn(Collection<? extends IdentifiableEmailWithMailboxIds> emails, String mailboxId) {
        for(IdentifiableEmailWithMailboxIds email : emails) {
            if (email.getMailboxIds().containsKey(mailboxId)) {
                return true;
            }
        }
        return false;
    }
}
