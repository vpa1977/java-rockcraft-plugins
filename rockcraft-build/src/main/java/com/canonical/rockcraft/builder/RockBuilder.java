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
package com.canonical.rockcraft.builder;


import java.io.IOException;
import java.nio.file.Files;

/**
 * Utilities to build rock image
 */
public class RockBuilder {

    private static final String ROCK_DIR = "rock";

    /**
     * Creates RockBuilder
     */
    public RockBuilder() {
    }

    /**
     * Checks that rockcraft is installed
     *
     * @throws InterruptedException - unable to start rockcraft
     * @throws IOException          - unable to start rockcraft
     */
    public static void checkRockcraft() throws InterruptedException, IOException {
        var pb = new ProcessBuilder("rockcraft", "--version");
        pb.inheritIO();
        var versionProcess = pb.start();
        int ret = versionProcess.waitFor();
        if (ret != 0)
            throw new UnsupportedOperationException("Please install rockcraft 'snap install rockcraft'.");
    }

    /**
     * Builds the rock image
     *
     * @param settings - rockcraft project settings
     * @throws IOException          - IO error while writing <i>rockcraft.yaml</i>
     * @throws InterruptedException - <i>rockcraft</i> process was aborted
     */
    public static void buildRock(RockProjectSettings settings) throws InterruptedException, IOException {
        var pb = new ProcessBuilder("rockcraft", "pack")
                .directory(settings.getRockOutput().toFile())
                .inheritIO();
        var process = pb.start();
        int result = process.waitFor();
        if (result != 0)
            throw new UnsupportedOperationException("Failed to pack rock for " + settings.getName());

        var rockDestPath = settings.getRockOutput().resolve(ROCK_DIR);
        var rockDest = rockDestPath.toFile();
        rockDest.mkdirs();
        for (var f : rockDest.listFiles((dir, file) -> file.endsWith(".rock"))) {
            f.delete();
        }
        // refresh rocks
        for (var f : settings.getRockOutput().toFile().listFiles((dir, file) -> file.endsWith(".rock"))) {
            var source = f.toPath();
            Files.move(source, rockDestPath.resolve(source.getFileName()));
        }
    }
}
