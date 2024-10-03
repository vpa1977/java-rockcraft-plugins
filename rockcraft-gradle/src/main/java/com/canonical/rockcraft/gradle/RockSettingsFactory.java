package com.canonical.rockcraft.gradle;

import org.gradle.api.Project;

import com.canonical.rockcraft.builder.RockProjectSettings;

public class RockSettingsFactory {
    public static final RockProjectSettings createRockProjectSettings(Project project) {
        var output = project.getLayout().getBuildDirectory().getAsFile().get().toPath();
        return new RockProjectSettings("gradle", project.getName(), String.valueOf(project.getVersion()), project.getProjectDir().toPath(),
            output);
    }
}
