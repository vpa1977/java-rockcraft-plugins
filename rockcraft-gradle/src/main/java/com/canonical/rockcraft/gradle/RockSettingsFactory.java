package com.canonical.rockcraft.gradle;

import org.gradle.api.Project;

import com.rockcraft.builder.RockProjectSettings;

public class RockSettingsFactory {
    public static final RockProjectSettings createRockProjectSettings(Project project) {
        return new RockProjectSettings("gradle", project.getName(), String.valueOf(project.getVersion()), project.getProjectDir().toPath());
    }
}
