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

package rs.ltt.jmap.common.util;

import rs.ltt.jmap.common.entity.filter.QueryString;

import java.util.Arrays;
import java.util.Comparator;

public class IndexableStringUtils {

    public static String toIndexableString(char a, char b, Object... objects) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < objects.length; ++i) {
            Object o = objects[i];
            if (i != 0) {
                builder.append(a);
            }
            if (o instanceof String[]) {
                String[] strings = (String[]) o;
                String[] copy = Arrays.copyOf(strings,strings.length);
                Arrays.sort(copy);
                for (int j = 0; j < copy.length; ++j) {
                    if (j != 0) {
                        builder.append(b);
                    }
                    append(builder, copy[j]);
                }
            } else if (o instanceof Object[]) {
                Object[] array = (Object[]) o;
                for (int j = 0; j < array.length; ++j) {
                    if (j != 0) {
                        builder.append(b);
                    }
                    append(builder, array[j]);
                }
            } else if (o instanceof Iterable) {
                int j = 0;
                for (Object element : (Iterable) o) {
                    if (j != 0) {
                        builder.append(b);
                    }
                    append(builder, element);
                    j++;
                }
            } else {
                append(builder, o);
            }
        }
        return builder.toString();
    }

    private static void append(StringBuilder builder, Object o) {
        if (o instanceof QueryString) {
            builder.append(((QueryString) o).toQueryString());
        } else if (o != null) {
            builder.append(o.toString());
        } else {
            builder.append('\00');
        }
    }

    public static class StringArrayComparator implements Comparator<String[]> {

        @Override
        public int compare(String[] a, String[] b) {
            if (a == null && b == null) {
                return 0;
            } else if (a != null && b == null) {
                return 1;
            } else if (a == null) {
                return -1;
            } else {
                String[] aCopy = Arrays.copyOf(a, a.length);
                String[] bCopy = Arrays.copyOf(b, b.length);
                Arrays.sort(aCopy);
                Arrays.sort(bCopy);
                return Arrays.toString(aCopy).compareTo(Arrays.toString(bCopy));
            }
        }
    }

}
