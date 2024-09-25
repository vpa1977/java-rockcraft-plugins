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

import org.gradle.testkit.runner.TaskOutcome;
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
        assertEquals(TaskOutcome.SUCCESS, result.getTasks().getLast().getOutcome()); // the build needs to succeed
    }

    @Test
    void validRockcraftYaml() throws IOException {
        runBuild("create-rock");
        try (var is = new FileInputStream(Path.of(getProjectDir().getAbsolutePath(), "build", "rockcraft.yaml").toFile())) {
            var yaml = new Yaml();
            Map<String, Object> parsed = yaml.load(is);
            assertEquals("ubuntu@24.04", parsed.get("build-base"));
            Map<String, Object> parts = (Map<String, Object>)parsed.get("parts");
            //
            Map<String, Object> dumpPart = (Map<String, Object>)parts.get("gradle/rockcraft/dump");
            assertTrue(dumpPart.containsKey("override-build"));
            Map<String, Object> runtimePart = (Map<String, Object>)parts.get("gradle/rockcraft/runtime");
            assertTrue(runtimePart.containsKey("override-build"));
            Map<String, Object> depsPart = (Map<String, Object>)parts.get("gradle/rockcraft/deps");
            assertTrue(depsPart.containsKey("override-build"));
        }
    }

    @Test
    void buildRockJava11Test() throws IOException {
        writeString(getBuildFile(), """
                plugins {
                    id('java')
                    id('io.github.vpa1977.rockcraft-plugin')
                }

                rockcraft {
                    buildPackage = "openjdk-11-jdk"
                    targetRelease = 11
                }

                """);
        var result = runBuild("build-rock");
        assertTrue(true); // the build needs to succeed
    }

    @Test
    void rockcraftPluginOptions() throws IOException {
        writeString(getBuildFile(), """
                plugins {
                    id('java')
                    id('io.github.vpa1977.rockcraft-plugin')
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
        runBuild("create-rock", "--stacktrace");
        try (var is = new FileInputStream(Path.of(getProjectDir().getAbsolutePath(), "build", "rockcraft.yaml").toFile())) {
            var yaml = new Yaml();
            Map<String, Object> parsed = yaml.load(is);
            assertEquals("Foobar", parsed.get("summary"));
        }
    }

    @Test
    void testArchitecture() throws IOException {
        writeString(getBuildFile(), """
                plugins {
                    id('java')
                    id('io.github.vpa1977.rockcraft-plugin')
                }

                rockcraft {
                    summary = "Foobar"
                    architectures = [ "amd64", "arm64" ]
                }

                """);
        runBuild("jar", "create-rock");

        try (var is = new FileInputStream(Path.of(getProjectDir().getAbsolutePath(), "build", "rockcraft.yaml").toFile())) {
            var yaml = new Yaml();
            Map<String, Object> parsed = yaml.load(is);
            Map<String, Object> platforms = (Map<String, Object>)parsed.get("platforms");
            assertTrue(platforms.containsKey("amd64"));
            assertTrue(platforms.containsKey("arm64"));
        }
    }

    @Test
    void onlySingleRockExists() throws IOException {
        writeString(getBuildFile(), """
            plugins {
                id('java')
                id('io.github.vpa1977.rockcraft-plugin')
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
                id('io.github.vpa1977.rockcraft-plugin')
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
