/**
 * Copyright 2025 Canonical Ltd.
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

import com.canonical.rockcraft.builder.IRockcraftNames;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DependencyExportTest extends BaseRockcraftTest {

    @Test
    public void testDefaultExport() {
        // The project should export dependencies without any dependant libraries
        BuildResult result = runBuild("dependencies-export", "--stacktrace");
        assertEquals(TaskOutcome.SUCCESS, getLastTaskOutcome(result)); // the build needs to succeed
        Path dependencies = projectDir.toPath().resolve("build/"+ IRockcraftNames.BUILD_ROCK_OUTPUT + "/" + IRockcraftNames.DEPENDENCIES_ROCK_OUTPUT);
        assertEquals(0, dependencies.toFile().list().length);
    }

    @Test
    public void testExportWithOptions() throws IOException {
        writeString(getBuildFile(), getResource("dependencies-options.in"));
        BuildResult result = runBuild("dependencies-export", "--stacktrace");
        assertEquals(TaskOutcome.SUCCESS, getLastTaskOutcome(result)); // the build needs to succeed

    }

    @Test
    public void testExportParent() throws IOException {
        // tests that parent pom for the artifact and
        // parent pom for the used bom are downloaded
        writeString(getBuildFile(), getResource("dependencies-parent.in"));
        BuildResult result = runBuild("dependencies-export", "--stacktrace");
        assertEquals(TaskOutcome.SUCCESS, getLastTaskOutcome(result)); // the build needs to succeed

        Path springBootWsParent = projectDir.toPath().resolve("build/" + IRockcraftNames.BUILD_ROCK_OUTPUT + "/" + IRockcraftNames.DEPENDENCIES_ROCK_OUTPUT + "/org/springframework/ws/spring-ws/4.0.3/spring-ws-4.0.3.pom");
        assertTrue(springBootWsParent.toFile().exists(), "Parent POM is downloaded");

        Path angus = projectDir.toPath().resolve("build/" + IRockcraftNames.BUILD_ROCK_OUTPUT + "/" + IRockcraftNames.DEPENDENCIES_ROCK_OUTPUT + "/org/eclipse/angus/jakarta.mail/1.0.0/jakarta.mail-1.0.0.pom");
        assertTrue(angus.toFile().exists(), "BOM is resolved");

        Path jacksonParent = projectDir.toPath().resolve("build/" + IRockcraftNames.BUILD_ROCK_OUTPUT + "/" + IRockcraftNames.DEPENDENCIES_ROCK_OUTPUT + "/com/fasterxml/jackson/jackson-parent/2.14/jackson-parent-2.14.pom");
        assertTrue(jacksonParent.toFile().exists(), "parent pom for BOM is downloaded");
    }

    @Test
    public void testExport() throws IOException {
        // assert that leaf jar file is downloaded, pom file is downloaded
        // unused bom is not downloaded
        // plugins are downloaded

        writeString(getBuildFile(), getResource("dependencies-build.in"));
        BuildResult result = runBuild("dependencies-export", "--stacktrace");
        assertEquals(TaskOutcome.SUCCESS, getLastTaskOutcome(result)); // the build needs to succeed
        // leaf jar and pom are downloaded
        Path springBoot = projectDir.toPath().resolve("build/" + IRockcraftNames.BUILD_ROCK_OUTPUT + "/" + IRockcraftNames.DEPENDENCIES_ROCK_OUTPUT + "/org/springframework/boot/spring-boot/3.0.6/spring-boot-3.0.6.jar");
        assertTrue(springBoot.toFile().exists(), "Spring Boot Jar is downloaded");
        Path springBootSha1 = projectDir.toPath().resolve("build/" + IRockcraftNames.BUILD_ROCK_OUTPUT + "/" + IRockcraftNames.DEPENDENCIES_ROCK_OUTPUT + "/org/springframework/boot/spring-boot/3.0.6/spring-boot-3.0.6.jar.sha1");
        String sha1 = Files.readString(springBootSha1);
        assertEquals("095ac2c7aa28fcdef587b2c4f554016f8b9af624", sha1);
        Path springBootPom = projectDir.toPath().resolve("build/" + IRockcraftNames.BUILD_ROCK_OUTPUT + "/" + IRockcraftNames.DEPENDENCIES_ROCK_OUTPUT + "/org/springframework/boot/spring-boot/3.0.6/spring-boot-3.0.6.pom");
        assertTrue(springBootPom.toFile().exists(), "Spring Boot POM is downloaded");

        // transitive boms are not downloaded
        Path springSecurityBOM = projectDir.toPath().resolve("build/" + IRockcraftNames.BUILD_ROCK_OUTPUT + "/" + IRockcraftNames.DEPENDENCIES_ROCK_OUTPUT + "/org/springframework/security/spring-security-bom/6.0.2/spring-security-bom-6.0.2.pom");
        assertFalse(springSecurityBOM.toFile().exists(), "transitive boms are not downloaded");

        // parent pom for unused bom is not downloaded
        Path springWsParent = projectDir.toPath().resolve("build/" + IRockcraftNames.BUILD_ROCK_OUTPUT + "/" + IRockcraftNames.DEPENDENCIES_ROCK_OUTPUT + "/org/springframework/ws/spring-ws/4.0.3/spring-ws-4.0.3.pom");
        assertFalse(springWsParent.toFile().exists(), "Parent bom of unused BOM is not downloaded");

        // plugin pom is downloaded
        Path pluginPom = projectDir.toPath().resolve("build/" + IRockcraftNames.BUILD_ROCK_OUTPUT + "/" + IRockcraftNames.DEPENDENCIES_ROCK_OUTPUT + "/io/gitlab/plunts/plantuml/io.gitlab.plunts.plantuml.gradle.plugin/2.2.0/io.gitlab.plunts.plantuml.gradle.plugin-2.2.0.pom");
        assertTrue(pluginPom.toFile().exists(), "Plugin POM is downloaded");

        // plugin jar is downloaded
        Path pluginJar = projectDir.toPath().resolve("build/" + IRockcraftNames.BUILD_ROCK_OUTPUT + "/" + IRockcraftNames.DEPENDENCIES_ROCK_OUTPUT + "/io/gitlab/plunts/plantuml-gradle-plugin/2.2.0/plantuml-gradle-plugin-2.2.0.jar");
        assertTrue(pluginJar.toFile().exists(), "Plugin JAR is downloaded");
    }
}
