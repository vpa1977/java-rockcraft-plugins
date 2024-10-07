package com.canonical.rockcraft.gradle;

import com.canonical.rockcraft.builder.RockProjectSettings;
import org.gradle.api.Project;

/**
 * Creates RockProjectSettings from Gradle project
 */
public class RockSettingsFactory {

    /**
     * Constructs RockSettingsFactory
     */
    RockSettingsFactory() {}

    /**
     * Creates RockProjectSettings from Gradle project
     *
     * @param project - gradle project
     * @return RockProjectSettings
     */
    public static final RockProjectSettings createRockProjectSettings(Project project) {
        return new RockProjectSettings("gradle", project.getName(),
            String.valueOf(project.getVersion()), project.getProjectDir().toPath(),
            project.getLayout().getBuildDirectory().getAsFile().get().toPath(),
                !project.getTasksByName("jlink", false).isEmpty());
    }
}
