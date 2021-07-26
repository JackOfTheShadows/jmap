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

package rs.ltt.jmap.common.entity;

import com.google.common.base.MoreObjects;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import rs.ltt.jmap.annotation.Type;

import java.util.Map;

@Getter
@Type
public class StateChange implements PushMessage {

    protected Map<String, Map<Class<? extends AbstractIdentifiableEntity>, String>> changed;

    @Builder
    public StateChange(@Singular("changed") Map<String, Map<Class<? extends AbstractIdentifiableEntity>, String>> changed) {
        this.changed = changed;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("changed", changed)
                .toString();
    }
}
