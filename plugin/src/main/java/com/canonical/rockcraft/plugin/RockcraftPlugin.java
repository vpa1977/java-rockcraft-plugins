/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.canonical.rockcraft.plugin;

import org.gradle.api.Project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

import org.gradle.api.Plugin;

/**
 * A simple 'hello world' plugin.
 */
public class RockcraftPlugin implements Plugin<Project> {
    private static final String ROCKCRAFT_YAML = "rockcraft.yaml";

    public void apply(Project project) {
        // Register a task
        var createRockcraft =
            project.getTasks().register("createRockcraft", CreateRockcraftTask.class);

        project.getTasks().register("buildROCK", task -> {
            task.setDependsOn(project.getTasksByName("generateROCK", false));
            task.doLast(s -> {
                //buildRock(project);
            });
        });
    }

    private void buildRock(Project project) throws IOException {
        throw new UnsupportedOperationException("Unimplemented method 'buildRock'");
    }

    private void generateRock(Project project) throws IOException {
        try (var templateReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(ROCKCRAFT_YAML)))) {

        }

        try (BufferedWriter wr = new BufferedWriter(new FileWriter(Path.of(getOutputDirectory(project), ROCKCRAFT_YAML).toFile()))) {
        }
     }

    private String getOutputDirectory(Project p) {
        return "build";
    }
}
