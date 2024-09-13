/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.canonical.rockcraft.plugin;

import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RockcraftPluginTest extends BaseRockcraftTest {

    @Test
    void validRockcraftYaml() throws IOException {
        BuildResult result = runBuild("jar");
        try (var is = new FileInputStream(Path.of(getProjectDir().getAbsolutePath(), "build", "rockcraft.yaml").toFile())) {
            var yaml = new Yaml();
            Map<String, Object> parsed = yaml.load(is);
            assertEquals("ubuntu@24.04", parsed.get("build-base"));
        }
    }

    @Test
    void rockcraftPluginOptions() throws IOException {
        writeString(getBuildFile(), """
                plugins {
                    id('java')
                    id('com.canonical.rockcraft-plugin')
                }

                rockcraft {
                    summary = "Foobar"
                    description = "readme.txt"
                }

                 """);
        writeString(new File(getProjectDir(), "readme.txt"), """
                This is a multiline description
                of the rock file
                """);
        BuildResult result = runBuild("jar");
        try (var is = new FileInputStream(Path.of(getProjectDir().getAbsolutePath(), "build", "rockcraft.yaml").toFile())) {
            var yaml = new Yaml();
            Map<String, Object> parsed = yaml.load(is);
        }
    }
}
