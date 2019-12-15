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


import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import rs.ltt.jmap.client.Version;

import java.io.IOException;

public class UserAgentInterceptor implements Interceptor {
    @Override
    @NonNullDecl
    public Response intercept(final Chain chain) throws IOException {
        final Request original = chain.request();
        final Request modified = original.newBuilder()
                .header("User-Agent",
                        String.format(
                                "%s/%s (%s)",
                                Version.ARTIFACT_ID, Version.VERSION, Version.URL
                        )
                )
                .build();
        return chain.proceed(modified);
    }
}
