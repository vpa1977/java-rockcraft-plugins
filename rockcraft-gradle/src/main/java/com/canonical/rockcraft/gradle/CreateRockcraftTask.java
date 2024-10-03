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

import com.canonical.rockcraft.builder.RockCrafter;
import com.canonical.rockcraft.builder.RockcraftOptions;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * This task writes <i>rockcraft.yaml</i> file for the application.
 */
public abstract class CreateRockcraftTask extends DefaultTask {

    private final RockcraftOptions options;

    /**
     * Constructs the CreateRockcraft task
     *
     * @param options - plugin options
     */
    @Inject
    public CreateRockcraftTask(RockcraftOptions options) {
        this.options = options;
    }

    private RockcraftOptions getOptions() {
        return options;
    }

    /**
     * Task action to write <i>rockcraft.yaml</i>
     */
    @TaskAction
    public void writeRockcraft() {
        HashSet<File> artifacts = new HashSet<File>();
        for (var conf : getProject().getConfigurations()) {
            artifacts.addAll(conf.getArtifacts().getFiles().getFiles().stream().filter(x -> x.getName().endsWith("jar")).toList());
        }

        try {
            var settings = RockSettingsFactory.createRockProjectSettings(getProject());
            RockCrafter crafter = new RockCrafter(settings, getOptions(), new ArrayList<File>(artifacts));
            crafter.writeRockcraft();
        } catch (IOException e) {
            throw new UnsupportedOperationException("Failed to write rockcraft.yaml: " + e.getMessage());
        }
    }
}
