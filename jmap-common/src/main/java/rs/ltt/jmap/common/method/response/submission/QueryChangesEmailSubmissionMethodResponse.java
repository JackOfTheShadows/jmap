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

package rs.ltt.jmap.common.method.response.submission;

import lombok.Builder;
import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.common.entity.AddedItem;
import rs.ltt.jmap.common.entity.EmailSubmission;
import rs.ltt.jmap.common.method.response.standard.QueryChangesMethodResponse;

import java.util.List;

@JmapMethod("EmailSubmission/queryChanges")
public class QueryChangesEmailSubmissionMethodResponse extends QueryChangesMethodResponse<EmailSubmission> {

    @Builder
    public QueryChangesEmailSubmissionMethodResponse(String accountId, String oldQueryState, String newQueryState, long total, String[] removed, List<AddedItem<String>> added) {
        super(accountId, oldQueryState, newQueryState, total, removed, added);
    }
}
