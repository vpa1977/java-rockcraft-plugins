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
import com.canonical.rockcraft.builder.RockcraftOptions;
import com.google.gradle.osdetector.OsDetector;
import com.google.gradle.osdetector.OsDetectorPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskProvider;

import java.io.IOException;

import java.util.Set;

/**
 * Gradle plugin for Rockcraft.
 * Allows to build rock images for Gradle projects.
 */
public class RockcraftPlugin implements Plugin<Project> {

    /**
     * Constructs RockcraftPlugin
     */
    public RockcraftPlugin() {
        super();
    }


    /**
     * Applies the plugin
     *
     * @param project The target object
     */
    public void apply(Project project) {

        project.getPlugins().apply(OsDetectorPlugin.class);

        RockcraftOptions options = project.getExtensions().create("rockcraft", RockcraftOptions.class);

        OsDetector detector = project.getExtensions().getByType(OsDetector.class);

        if (!"linux".equals(detector.getOs()))
            throw new UnsupportedOperationException("Rockcraft is only supported on linux systems");

        TaskProvider<Task> checkTask = project.getTasks().register("checkRockcraft", s -> {
            s.doFirst(x -> {
                try {
                    RockBuilder.checkRockcraft();
                } catch (IOException | InterruptedException e) {
                    throw new UnsupportedOperationException(e.getMessage());
                }
            });
        });

        Set<Task> buildTasks = project.getTasksByName("build", false);
        if (buildTasks.isEmpty())
            throw new UnsupportedOperationException("Rockcraft plugin requires build task");

        for (Task t : buildTasks)
            t.finalizedBy(checkTask);

        Set<Task> tasks = project.getTasksByName(ITaskNames.JLINK, false);
        if (tasks.isEmpty())
            tasks = project.getTasksByName(ITaskNames.BOOT_JAR, false);
        if (tasks.isEmpty())
            tasks = project.getTasksByName(ITaskNames.JAR, false);
        if (tasks.isEmpty())
            throw new UnsupportedOperationException("Rockcraft plugin requires bootJar or jar task");

        TaskProvider<PushRockcraftTask> push = project.getTasks().register("push-rock", PushRockcraftTask.class, options);
        TaskProvider<BuildRockcraftTask> build = project.getTasks().register("build-rock", BuildRockcraftTask.class, options);
        TaskProvider<CreateRockcraftTask> create = project.getTasks().register("create-rock", CreateRockcraftTask.class, options);

        project.getTasks().getByName("push-rock")
                .dependsOn(build);

        project.getTasks().getByName("build-rock")
                .dependsOn(create);

        project.getTasks().getByName("create-rock")
                .dependsOn(tasks);
    }
}
