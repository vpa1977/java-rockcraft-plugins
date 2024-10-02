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
package com.canonical.rockcraft.gradle;

import com.canonical.rockcraft.builder.RockBuilder;
import com.canonical.rockcraft.builder.RockProjectSettings;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;

/**
 * This task builds a ROCK image by calling <i>rockcraft pack</i>.
 * It removes all previous ROCK artifacts from the build directory.
 */
public class BuildRockcraftTask extends DefaultTask {

    /**
     * Constructs BuildRockcraft task
     */
    public BuildRockcraftTask() {
        super();
    }

    /**
     * The task action
     * @throws IOException - IO error while writing <i>rockcraft.yaml</i>
     * @throws InterruptedException - <i>rockcraft</i> process was aborted
     */
    @TaskAction
    public void packRock() throws IOException, InterruptedException {
        var buildDir = getProject().getLayout().getBuildDirectory().getAsFile().get();
        var settings = RockSettingsFactory.createRockProjectSettings(getProject());
        RockBuilder.buildRock(settings, buildDir);
    }
}
