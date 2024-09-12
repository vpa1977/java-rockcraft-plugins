package com.canonical.rockcraft.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class BuildRockcraftTask extends DefaultTask {

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
    }
}
