package com.canonical.rockcraft.gradle;

import com.canonical.rockcraft.builder.IRockcraftNames;
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

@Tag("buildRock")
public class DefaultBuildRockcraftLocationTest extends BaseRockcraftTest {

    @BeforeEach
    protected void setUp() throws IOException {
        super.setUp();
        File buildRockDir = new File(getProjectDir(), IRockcraftNames.BUILD_ROCK_OUTPUT);
        buildRockDir.mkdirs();
        writeString(new File(buildRockDir, "rockcraft.yaml"), getResource("default-build-rock-rockcraft.in"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDefaultBuildRockcraftYamlLocation() throws IOException {
        BuildResult result = runBuild(ITaskNames.CREATE_BUILD_ROCK, "--stacktrace");
        assertEquals(TaskOutcome.SUCCESS, getLastTaskOutcome(result));
        try (FileInputStream is = new FileInputStream(Paths.get(getProjectDir().getAbsolutePath(), "build", IRockcraftNames.BUILD_ROCK_OUTPUT, IRockcraftNames.ROCKCRAFT_YAML).toFile())) {
            Yaml yaml = new Yaml();
            Map<String, Object> parsed = yaml.load(is);
            assertEquals("default-location-build-rock", parsed.get("name"));
        }
    }
}
