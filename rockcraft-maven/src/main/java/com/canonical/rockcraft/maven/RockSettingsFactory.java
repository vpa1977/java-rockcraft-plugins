/**
 * Copyright 2024 Canonical Ltd.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.canonical.rockcraft.maven;

import com.canonical.rockcraft.builder.Generator;
import com.canonical.rockcraft.builder.IRockcraftNames;
import com.canonical.rockcraft.builder.RockProjectSettings;
import org.apache.maven.project.MavenProject;
import org.apache.maven.rtinfo.RuntimeInformation;

import java.nio.file.Paths;

/**
 * Creates RockProjectSettings from Maven project
 */
public final class RockSettingsFactory {

    /**
     * Constructs RockSettingsFactory
     */
    RockSettingsFactory() {}

    /**
     * Creates RockProjectSettings from Maven project
     *
     * @param project - Maven project
     * @return RockProjectSettings
     */
    public static RockProjectSettings createRockProjectSettings(RuntimeInformation info, MavenProject project) {
        return new RockProjectSettings(Generator.maven, info.getMavenVersion(), project.getName(),
                project.getVersion(), project.getBasedir().getAbsoluteFile().toPath(),
                Paths.get(project.getBuild().getDirectory()),
                false);
    }

    /**
     * Creates RockProjectSettings from Gradle project for the build rock
     *
     * @param project - gradle project
     * @return RockProjectSettings
     */
    public static RockProjectSettings createBuildRockProjectSettings(RuntimeInformation info, MavenProject project) {

        return new RockProjectSettings(Generator.maven, info.getMavenVersion(),
                project.getName(), String.valueOf(project.getVersion()),
                project.getBasedir().getAbsoluteFile().toPath(),
                Paths.get(project.getBuild().getDirectory()).resolve(IRockcraftNames.BUILD_ROCK_OUTPUT),
                false);
    }
}
