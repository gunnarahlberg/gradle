/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.logging.internal;

import org.gradle.internal.TimeProvider;
import org.gradle.logging.ProgressLogger;
import org.gradle.logging.ProgressLoggerFactory;
import org.gradle.util.GUtil;

public class DefaultProgressLoggerFactory implements ProgressLoggerFactory {
    private final ProgressListener progressListener;
    private final TimeProvider timeProvider;

    public DefaultProgressLoggerFactory(ProgressListener progressListener, TimeProvider timeProvider) {
        this.progressListener = progressListener;
        this.timeProvider = timeProvider;
    }

    public ProgressLogger newOperation(Class loggerCategory) {
        return newOperation(loggerCategory.getName());
    }

    public ProgressLogger newOperation(String loggerCategory) {
        return new ProgressLoggerImpl(loggerCategory, progressListener, timeProvider);
    }

    private static class ProgressLoggerImpl implements ProgressLogger {
        private enum State { idle, started, completed }
        private final String category;
        private final ProgressListener listener;
        private final TimeProvider timeProvider;
        private String description;
        private String shortDescription;
        private String loggingHeader;
        private State state = State.idle;

        public ProgressLoggerImpl(String category, ProgressListener listener, TimeProvider timeProvider) {
            this.category = category;
            this.listener = listener;
            this.timeProvider = timeProvider;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            assertCanConfigure();
            this.description = description;
        }

        public String getShortDescription() {
            return shortDescription;
        }

        public void setShortDescription(String shortDescription) {
            assertCanConfigure();
            this.shortDescription = shortDescription;
        }

        public String getLoggingHeader() {
            return loggingHeader;
        }

        public void setLoggingHeader(String loggingHeader) {
            assertCanConfigure();
            this.loggingHeader = loggingHeader;
        }

        public void started() {
            started(null);
        }

        public void started(String status) {
            if (!GUtil.isTrue(description)) {
                throw new IllegalStateException("A description must be specified before this operation is started.");
            }
            if (state == State.started) {
                throw new IllegalStateException("This operation has already been started.");
            }
            assertNotCompleted();
            state = State.started;
            listener.started(new ProgressStartEvent(timeProvider.getCurrentTime(), category, description, shortDescription, loggingHeader, toStatus(status)));
        }

        public void progress(String status) {
            assertStarted();
            assertNotCompleted();
            listener.progress(new ProgressEvent(timeProvider.getCurrentTime(), category, toStatus(status)));
        }

        public void completed() {
            completed(null);
        }

        public void completed(String status) {
            assertStarted();
            assertNotCompleted();
            state = State.completed;
            listener.completed(new ProgressCompleteEvent(timeProvider.getCurrentTime(), category, toStatus(status)));
        }

        private String toStatus(String status) {
            return status == null ? "" : status;
        }

        private void assertNotCompleted() {
            if (state == ProgressLoggerImpl.State.completed) {
                throw new IllegalStateException("This operation has completed.");
            }
        }

        private void assertStarted() {
            if (state == ProgressLoggerImpl.State.idle) {
                throw new IllegalStateException("This operation has not been started.");
            }
        }

        private void assertCanConfigure() {
            if (state != State.idle) {
                throw new IllegalStateException("Cannot configure this operation once it has started.");
            }
        }
    }
}
