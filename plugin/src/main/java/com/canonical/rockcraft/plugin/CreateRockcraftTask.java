package com.canonical.rockcraft.plugin;

import java.io.IOException;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class CreateRockcraftTask extends DefaultTask {

    @TaskAction
    public void writeRockcraft() throws IOException {
        throw new IOException();

    }
}
