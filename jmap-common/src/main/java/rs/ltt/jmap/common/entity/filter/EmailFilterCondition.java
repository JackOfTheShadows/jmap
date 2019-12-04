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

package rs.ltt.jmap.common.entity.filter;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import lombok.Builder;
import lombok.Getter;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import rs.ltt.jmap.common.entity.Email;
import rs.ltt.jmap.common.util.IndexableStringUtils;

@Getter
@Builder
public class EmailFilterCondition implements FilterCondition<Email> {

    private String inMailbox;

    private String[] inMailboxOtherThan;

    private Long minSize;

    private Long maxSize;

    private String allInThreadHaveKeyword;

    private String someInThreadHaveKeyword;

    private String noneInThreadHaveKeyword;

    private String hasKeyword;

    private String notKeyword;

    private Boolean hasAttachment;

    private String text;

    private String from;

    private String to;

    private String cc;

    private String bcc;

    private String subject;

    private String body;

    @Override
    public String toQueryString() {
        return IndexableStringUtils.toIndexableString(
                L3_DIVIDER,
                L4_DIVIDER,
                inMailbox,
                inMailboxOtherThan,
                minSize,
                maxSize,
                allInThreadHaveKeyword,
                someInThreadHaveKeyword,
                noneInThreadHaveKeyword,
                hasKeyword,
                notKeyword,
                hasAttachment,
                text,
                from,
                to,
                cc,
                bcc,
                subject,
                body
        );
    }

    @Override
    public int compareTo(@NonNullDecl Filter<Email> filter) {
        if (filter instanceof EmailFilterCondition) {
            EmailFilterCondition other = (EmailFilterCondition) filter;
            return ComparisonChain.start()
                    .compare(Strings.nullToEmpty(inMailbox), Strings.nullToEmpty(other.inMailbox))
                    .compare(inMailboxOtherThan, other.inMailboxOtherThan, new IndexableStringUtils.StringArrayComparator())
                    .compare(minSize == null ? 0L : minSize, other.minSize == null ? 0L : other.minSize)
                    .compare(maxSize == null ? 0L : maxSize, other.maxSize == null ? 0L : other.maxSize)
                    .compare(Strings.nullToEmpty(allInThreadHaveKeyword), Strings.nullToEmpty(other.allInThreadHaveKeyword))
                    .compare(Strings.nullToEmpty(someInThreadHaveKeyword), Strings.nullToEmpty(other.someInThreadHaveKeyword))
                    .compare(Strings.nullToEmpty(noneInThreadHaveKeyword), Strings.nullToEmpty(other.noneInThreadHaveKeyword))
                    .compare(Strings.nullToEmpty(hasKeyword), Strings.nullToEmpty(other.hasKeyword))
                    .compare(Strings.nullToEmpty(notKeyword), Strings.nullToEmpty(other.notKeyword))
                    .compareFalseFirst(nullToFalse(hasAttachment), nullToFalse(other.hasAttachment))
                    .compare(Strings.nullToEmpty(text), Strings.nullToEmpty(other.text))
                    .compare(Strings.nullToEmpty(from), Strings.nullToEmpty(other.from))
                    .compare(Strings.nullToEmpty(cc), Strings.nullToEmpty(other.cc))
                    .compare(Strings.nullToEmpty(bcc), Strings.nullToEmpty(other.bcc))
                    .compare(Strings.nullToEmpty(subject), Strings.nullToEmpty(other.subject))
                    .compare(Strings.nullToEmpty(body), Strings.nullToEmpty(other.body))
                    .result();
        } else {
            return 1;
        }
    }

    private static boolean nullToFalse(Boolean b) {
        return b == null ? false : b;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("inMailbox", inMailbox)
                .add("inMailboxOtherThan", inMailboxOtherThan)
                .add("minSize", minSize)
                .add("maxSize", maxSize)
                .add("allInThreadHaveKeyword", allInThreadHaveKeyword)
                .add("someInThreadHaveKeyword", someInThreadHaveKeyword)
                .add("noneInThreadHaveKeyword", noneInThreadHaveKeyword)
                .add("hasKeyword", hasKeyword)
                .add("notKeyword", notKeyword)
                .add("hasAttachment", hasAttachment)
                .add("text", text)
                .add("from", from)
                .add("to", to)
                .add("cc", cc)
                .add("bcc", bcc)
                .add("subject", subject)
                .add("body", body)
                .omitNullValues()
                .toString();
    }
}
