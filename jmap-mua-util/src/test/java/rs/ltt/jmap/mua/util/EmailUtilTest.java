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

import org.junit.Assert;
import org.junit.Test;
import rs.ltt.jmap.common.entity.Email;

public class EmailUtilTest {

    @Test
    public void firstResponseSubject() {
        Assert.assertEquals(
                "Re: Hello",
                EmailUtil.getResponseSubject(
                        Email.builder()
                                .subject("Hello")
                                .build()
                )
        );
    }

    @Test
    public void secondResponseSubject() {
        Assert.assertEquals(
                "Re: Hello",
                EmailUtil.getResponseSubject(
                        Email.builder()
                                .subject("RE: Hello")
                                .build()
                )
        );
    }

    @Test
    public void secondResponseSubjectExcessWhitespace() {
        Assert.assertEquals(
                "Re: Hello",
                EmailUtil.getResponseSubject(
                        Email.builder()
                                .subject("RE:    Hello  ")
                                .build()
                )
        );
    }

    @Test
    public void secondResponseSubjectGerman() {
        Assert.assertEquals(
                "Re: Hello",
                EmailUtil.getResponseSubject(
                        Email.builder()
                                .subject("Aw: Hello")
                                .build()
                )
        );
    }
}
