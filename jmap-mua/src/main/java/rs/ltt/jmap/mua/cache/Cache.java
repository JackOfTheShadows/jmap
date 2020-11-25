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

package rs.ltt.jmap.mua.cache;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import rs.ltt.jmap.common.entity.Thread;
import rs.ltt.jmap.common.entity.*;
import rs.ltt.jmap.mua.util.QueryResult;
import rs.ltt.jmap.mua.util.QueryResultItem;

import java.util.Collection;

public interface Cache {

    //states
    String getIdentityState();

    String getMailboxState();

    @NonNullDecl
    QueryStateWrapper getQueryState(@NullableDecl String query);

    @NonNullDecl
    ObjectsState getObjectsState();

    void setMailboxes(TypedState<Mailbox> state, Mailbox[] mailboxes) throws CacheWriteException;

    void updateMailboxes(Update<Mailbox> mailboxUpdate, String[] updatedProperties) throws CacheWriteException, CacheConflictException;

    Collection<? extends IdentifiableMailboxWithRole> getSpecialMailboxes() throws NotSynchronizedException;

    void setThreadsAndEmails(TypedState<Thread> threadState, Thread[] threads, TypedState<Email> emailState, Email[] emails);

    void addThreadsAndEmail(TypedState<Thread> threadState, Thread[] threads, TypedState<Email> emailState, Email[] emails);

    void updateThreads(Update<Thread> threadUpdate) throws CacheWriteException, CacheConflictException;

    void updateEmails(Update<Email> emailUpdate, String[] updatedProperties) throws CacheWriteException, CacheConflictException;

    void invalidateEmails();

    void invalidateThreads();

    void invalidateMailboxes();


    //Identity
    void setIdentities(TypedState<Identity> state, Identity[] identities) throws CacheWriteException;


    void updateIdentities(Update<Identity> identityUpdate) throws CacheWriteException, CacheConflictException;

    void invalidateIdentities();

    //Queries

    void setQueryResult(String queryString, QueryResult queryResult) throws CacheWriteException;

    void addQueryResult(String queryString, String afterId, QueryResult queryResult) throws CacheWriteException, CacheConflictException;

    void updateQueryResults(String queryString, QueryUpdate<Email, QueryResultItem> update, TypedState<Email> emailState) throws CacheWriteException, CacheConflictException;

    void invalidateQueryResult(String queryString);

    Missing getMissing(String query) throws CacheReadException;
}

