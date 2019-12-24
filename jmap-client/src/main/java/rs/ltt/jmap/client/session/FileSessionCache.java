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

package rs.ltt.jmap.client.session;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.HttpUrl;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rs.ltt.jmap.gson.JmapAdapters;

import java.io.*;

public class FileSessionCache implements SessionCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSessionCache.class);

    private final File directory;

    private final GsonBuilder gsonBuilder = new GsonBuilder();

    public FileSessionCache() {
        JmapAdapters.register(this.gsonBuilder);
        this.directory = null;
    }

    public FileSessionCache(@NonNullDecl File directory) {
        JmapAdapters.register(this.gsonBuilder);
        this.directory = directory;
        LOGGER.debug("Initialize cache in {}", directory.getAbsolutePath());
    }

    @Override
    public void store(String username, HttpUrl sessionResource, Session session) {
        final File file = getFile(getFilename(username, sessionResource));
        final Gson gson = this.gsonBuilder.create();
        try {
            final FileWriter fileWriter = new FileWriter(file);
            gson.toJson(session, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            LOGGER.error("Unable to cache session in {}", file.getAbsolutePath());
        }
    }

    private File getFile(final String filename) {
        if (directory == null) {
            return new File(filename);
        } else {
            return new File(directory, filename);
        }
    }

    private static String getFilename(String username, HttpUrl sessionResource) {
        final String name = username + ':' + (sessionResource == null ? '\00' : sessionResource.toString());
        return "session-cache-" + Hashing.sha256().hashString(name, Charsets.UTF_8).toString();
    }

    @Override
    public Session load(String username, HttpUrl sessionResource) {
        final File file = getFile(getFilename(username, sessionResource));
        final Gson gson = this.gsonBuilder.create();
        try {
            final Session session = gson.fromJson(new FileReader(file), Session.class);
            LOGGER.debug("Restored session from {}", file.getAbsolutePath());
            return session;
        } catch (final FileNotFoundException e) {
            LOGGER.debug("Unable to restore session. {} not found", file.getAbsolutePath());
            return null;
        } catch (final Exception e) {
            LOGGER.warn("Unable to restore session", e);
            return null;
        }
    }
}
