/**
 * Copyright 2024 Canonical Ltd.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.canonical.rockcraft.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Files;

/**
 * This task builds a ROCK image by calling <i>rockcraft pack</i>.
 * It removes all previous ROCK artifacts from the build directory.
 */
public class BuildRockcraftTask extends DefaultTask {

    private static final String ROCK_DIR = "rock";

    /**
     * Constructs BuildRockcraft task
     */
    public BuildRockcraftTask() { super();}
    /**
     * The task action
     * @throws IOException - IO error while writing <i>rockcraft.yaml</i>
     * @throws InterruptedException - <i>rockcraft</i> process was aborted
     */
    @TaskAction
    public void packRock() throws IOException, InterruptedException {
        var buildDir = getProject().getLayout().getBuildDirectory();
        var pb = new ProcessBuilder("rockcraft", "pack")
                .directory(buildDir.getAsFile().get())
                .inheritIO();
        var process = pb.start();
        int result = process.waitFor();
        if (result != 0)
            throw new UnsupportedOperationException("Failed to pack rock for "+ getProject().getName());

        var rockDest = buildDir.dir(ROCK_DIR).get().getAsFile();
        rockDest.mkdirs();
        for (var f : rockDest.listFiles((dir,file) -> file.endsWith(".rock"))) {
            f.delete();
        }
        // refresh rocks
        for (var f : buildDir.getAsFile().get().listFiles((dir,file) -> file.endsWith(".rock"))) {
            var source = f.toPath();
            var dest = buildDir.dir(ROCK_DIR).get().getAsFile().toPath();
            Files.move(source, dest.resolve(source.getFileName()));
        }
    }
}
