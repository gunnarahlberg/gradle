/*
 * Copyright 2011 the original author or authors.
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
package org.gradle.api.internal.artifacts.repositories.layout;

import org.apache.ivy.plugins.resolver.RepositoryResolver;

import java.net.URI;

/**
 * A Repository Layout that applies the following patterns:
 * <ul>
 *     <li>Artifacts: $baseUri/[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier])(.[ext])</li>
 *     <li>Ivy: $baseUri/[organisation]/[module]/[revision]/ivy-[revision].xml</li>
 * </ul>
 *
 * Note the pattern is the same for both artifacts and ivy files.
 */
public class GradleRepositoryLayout extends RepositoryLayout {
    public void apply(URI baseUri, RepositoryResolver resolver) {
        if (baseUri == null) {
            return;
        }

        ResolvedPattern artifactPattern = new ResolvedPattern(baseUri, "[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier])(.[ext])");
        resolver.addArtifactPattern(artifactPattern.absolutePattern);

        ResolvedPattern ivyPattern = new ResolvedPattern(baseUri, "[organisation]/[module]/[revision]/ivy-[revision].xml");
        resolver.addIvyPattern(ivyPattern.absolutePattern);
    }
}