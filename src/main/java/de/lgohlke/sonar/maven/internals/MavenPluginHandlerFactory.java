/*
 * Sonar maven checks plugin
 * Copyright (C) 2012 Lars Gohlke
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package de.lgohlke.sonar.maven.internals;

import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.resources.Project;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * wraps the {@link MavenPluginHandler} implementation, into a generic
 * 
 * @author Lars Gohlke
 */
public class MavenPluginHandlerFactory {

  public static MavenPluginHandler createHandler(final String groupArtifactVersionGoalString) {
    checkNotNull(groupArtifactVersionGoalString);
    checkState(groupArtifactVersionGoalString.length() > 0, "no empty string allowed");

    final String[] parts = groupArtifactVersionGoalString.split(":");

    checkArgument(parts.length == 4, "the string must be consist of four parts, seperated by : e.g.: org.codehaus.mojo:versions-maven-plugin:1.3.1:help ");

    return new MavenPluginHandler() {

      @Override
      public boolean isFixedVersion() {
        // so far it makes no sense, to be not fixed to a specific version
        return true;
      }

      @Override
      public String getVersion() {
        return parts[2];
      }

      @Override
      public String getGroupId() {
        return parts[0];
      }

      @Override
      public String[] getGoals() {
        return new String[] {parts[3]};
      }

      @Override
      public String getArtifactId() {
        return parts[1];
      }

      @Override
      public void configure(final Project project, final MavenPlugin plugin) {
        // no need for
      }
    };
  }

}