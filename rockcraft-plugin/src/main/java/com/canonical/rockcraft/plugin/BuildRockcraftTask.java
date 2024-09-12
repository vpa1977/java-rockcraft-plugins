package com.canonical.rockcraft.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

public class BuildRockcraftTask extends DefaultTask {

    private static final String ROCK_DIR = "rock";

    @TaskAction
    public void packRock() throws IOException, InterruptedException {
        var buildDir = getProject().getLayout().getBuildDirectory();
        var pb = new ProcessBuilder("rockcraft", "pack")
                .directory(buildDir.getAsFile().get())
                .inheritIO();
        var process = pb.start();
        int result = process.waitFor();
        if (result != 0)
            throw new UnsupportedEncodingException("Failed to pack rock for "+ getProject().getName());

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
