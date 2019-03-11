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

package rs.ltt.jmap.common.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Builder
@Getter
public class EmailSubmission extends AbstractIdentifiableEntity {

    private String identityId;
    private String emailId;
    private String threadId;
    private Envelope envelope;
    private Date sendAt;
    private UndoStatus undoStatus;

    @Singular("deliveryStatus")
    private Map<String,DeliveryStatus> deliveryStatus;

    @Singular
    private List<String> dsnBlobIds;

    @Singular
    private List<String> mdnBlobIds;


}
