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

package rs.ltt.jmap.common.method.call.email;

import lombok.Builder;
import lombok.Getter;
import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.common.entity.Comparator;
import rs.ltt.jmap.common.entity.Email;
import rs.ltt.jmap.common.entity.filter.Filter;
import rs.ltt.jmap.common.entity.query.EmailQuery;
import rs.ltt.jmap.common.method.call.standard.QueryMethodCall;

@JmapMethod("Email/query")
@Getter
public class QueryEmailMethodCall extends QueryMethodCall<Email> {

    private Boolean collapseThreads;

    @Builder
    public QueryEmailMethodCall(String accountId, Filter<Email> filter, Comparator[] sort, Long position, String anchor,
                                Long anchorOffset, Long limit, Boolean collapseThreads, Boolean calculateTotal) {
        super(accountId, filter, sort, position, anchor, anchorOffset, limit, calculateTotal);
        this.collapseThreads = collapseThreads;
    }

    public static class QueryEmailMethodCallBuilder {
        public QueryEmailMethodCallBuilder query(EmailQuery query) {
            filter(query.filter);
            sort(query.comparators);
            collapseThreads(query.collapseThreads);
            return this;
        }
    }
}
