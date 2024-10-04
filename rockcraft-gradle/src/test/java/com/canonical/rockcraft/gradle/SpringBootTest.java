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
package com.canonical.rockcraft.gradle;

import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that rockcraft plugin can deploy a Spring Boot sample
 */
public class SpringBootTest extends BaseRockcraftTest {

    @BeforeEach
    protected void setUp() throws IOException {
        String app = """
                package com.example.app2;

                import org.springframework.boot.SpringApplication;
                import org.springframework.boot.autoconfigure.SpringBootApplication;

                @SpringBootApplication
                public class DemoApplication {

                	public static void main(String[] args) {
                		SpringApplication.run(DemoApplication.class, args);
                	}

                }
                """;
        Path javaDir = Path.of(projectDir.getAbsolutePath(), "src", "main", "java", "com", "example", "app2");
        assertTrue(javaDir.toFile().mkdirs());
        writeString(javaDir.resolve("DemoApplication.java").toFile(), app);
        String gradle = """
                plugins {
                	id 'java'
                	id 'org.springframework.boot' version '3.3.2'
                	id 'io.spring.dependency-management' version '1.1.6'
                	id 'io.rockcrafters.rockcraft'
                }

                group = 'com.example'
                version = '0.0.1-SNAPSHOT'

                repositories {
                	mavenCentral()
                }

                dependencies {
                	implementation 'org.springframework.boot:spring-boot-starter'
                	testImplementation 'org.springframework.boot:spring-boot-starter-test'
                	testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
                }
                """;
        writeString(getBuildFile(), gradle);
        writeString(getSettingsFile(), "rootProject.name = 'app2'\n");
    }

    @Test
    public void springBootHelloWorld() throws IOException {
        var result = runBuild("build-rock", "--stacktrace");
        assertEquals(TaskOutcome.SUCCESS, result.getTasks().getLast().getOutcome()); // the build needs to succeed
    }
}
