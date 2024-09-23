package com.canonical.rockcraft.plugin;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import org.gradle.testkit.runner.BuildResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class BaseRockcraftTest {

    @TempDir
    protected File projectDir;

    protected File getJavaSource() {
        return Path.of(projectDir.getAbsolutePath(), "src", "main", "java", "Test.java").toFile();
    }

    protected File getProjectDir() { return projectDir; }

    protected File getBuildFile() {
        return new File(projectDir, "build.gradle");
    }

    protected File getSettingsFile() {
        return new File(projectDir, "settings.gradle");
    }

    protected void writeString(File file, String string) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }

    @BeforeEach
    protected void setUp() throws IOException {
        assertTrue(Path.of(projectDir.getAbsolutePath(), "src", "main", "java").toFile().mkdirs());
        writeString(getJavaSource(),
                """
                        public class Test {
                            public static void main(String[] args) {
                                System.out.println("Hello!");
                            }
                        }
                        """);
        writeString(getSettingsFile(), "");
        writeString(getBuildFile(),
                """
                        plugins {
                            id('application')
                            id('io.github.vpa1977.rockcraft-plugin')
                        }
                        """);
    }

    public BuildResult runBuild(String... target) {
        GradleRunner runner = GradleRunner.create();
        runner.forwardOutput();
        runner.withPluginClasspath();
        runner.withArguments(target);
        runner.withDebug(true);
        runner.withProjectDir(projectDir);
        return runner.build();
    }

}
