package com.canonical.rockcraft.gradle;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("rock")
public class DefaultRockcraftLocationTest extends BaseRockcraftTest {

    @BeforeEach
    protected void setUp() throws IOException {
        super.setUp();
        File rockDir = new File(getProjectDir(), "rock");
        rockDir.mkdirs();
        writeString(new File(rockDir, "rockcraft.yaml"), getResource("default-rock-rockcraft.in"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDefaultRockcraftYamlLocation() throws IOException {
        BuildResult result = runBuild(ITaskNames.CREATE_ROCK, "--stacktrace");
        assertEquals(TaskOutcome.SUCCESS, getLastTaskOutcome(result));
        try (FileInputStream is = new FileInputStream(Paths.get(getProjectDir().getAbsolutePath(), "build", "rockcraft.yaml").toFile())) {
            Yaml yaml = new Yaml();
            Map<String, Object> parsed = yaml.load(is);
            assertEquals("default location summary", parsed.get("summary"));
        }
    }
}
