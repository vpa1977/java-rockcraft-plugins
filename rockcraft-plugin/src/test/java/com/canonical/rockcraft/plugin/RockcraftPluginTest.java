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
    void buildRockTest() throws IOException {
        var result = runBuild("build-rock");
        assertTrue(true); // the build needs to succeed
    }

    @Test
    void validRockcraftYaml() throws IOException {
        runBuild("create-rock");
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
        runBuild("create-rock");
        try (var is = new FileInputStream(Path.of(getProjectDir().getAbsolutePath(), "build", "rockcraft.yaml").toFile())) {
            var yaml = new Yaml();
            Map<String, Object> parsed = yaml.load(is);
            assertEquals("Foobar", parsed.get("summary"));
        }
    }

    @Test
    void onlySingleRockExists() throws IOException {
        writeString(getBuildFile(), """
            plugins {
                id('java')
                id('com.canonical.rockcraft-plugin')
            }

            version = 0.01

            rockcraft {
            }

            """);
        runBuild("build-rock");
        File output = Path.of(getProjectDir().getAbsolutePath(), "build","rock").toFile();
        assertEquals(1, output.list( (dir, name) -> name.endsWith("rock")).length);

        writeString(getBuildFile(), """
            plugins {
                id('java')
                id('com.canonical.rockcraft-plugin')
            }

            version = '0.02updated'

            rockcraft {
            }

            """);
        runBuild("build-rock");
        String[] rocks = output.list( (dir, name) -> name.endsWith("rock"));
        assertEquals(1, rocks.length);
        assertTrue(rocks[0].contains("0.02updated"));
    }
}
