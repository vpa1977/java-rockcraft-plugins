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

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.yaml.snakeyaml.Yaml;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public abstract class CreateRockcraftTask extends DefaultTask {

    private static final String ROCKCRAFT_YAML = "rockcraft.yaml";

    private final RockcraftOptions options;

    private RockcraftOptions getOptions() {
        return options;
    }

    @Inject
    public CreateRockcraftTask(RockcraftOptions options) {
        this.options = options;
    }

    @TaskAction
    public void writeRockcraft() {
        getProject().getConfigurations().getByName("archives", archives -> {
            try {
                var buildDir = getProject().getLayout().getBuildDirectory();
                try (BufferedWriter wr = new BufferedWriter(new FileWriter(buildDir.file(ROCKCRAFT_YAML).get().getAsFile()))) {
                    var files = archives.getArtifacts().getFiles().getFiles().stream().filter(x -> x.getName().endsWith("jar")).toList();
                    wr.write(createRockcraft(buildDir.getAsFile().get().toPath(), files));
                }
            } catch (IOException e) {
                throw new UnsupportedOperationException("Failed to write rockcraft.yaml: " + e.getMessage());
            }
        });
    }

    protected String createRockcraft(Path root, List<File> files) throws IOException {
        List<String> relativeJars = new ArrayList<String>();
        for (var file : files)
            relativeJars.add(root.relativize(file.toPath()).toString());

        var rockcraft = new HashMap<String, Object>();
        rockcraft.put("name", getProject().getName());
        rockcraft.put("version", String.valueOf(getProject().getVersion()));
        rockcraft.put("summary", getOptions().getSummary());
        var description = getOptions().getDescription();
        if (!description.isEmpty()) {
            var descriptionFile = getProject().getLayout().getProjectDirectory().file(description).getAsFile();
            if (!descriptionFile.exists())
                throw new UnsupportedOperationException("Rockcraft plugin description file does not exist.");
            rockcraft.put("description", new String(Files.readAllBytes(descriptionFile.toPath())));
        } else {
            rockcraft.put("description", "");
        }

        rockcraft.put("platforms", getPlatforms());
        rockcraft.put("base", "bare");
        rockcraft.put("build-base", "ubuntu@24.04");
        rockcraft.put("services", getProjectService(relativeJars));
        rockcraft.put("parts", getProjectParts(files, relativeJars));
        return new Yaml().dump(rockcraft);
    }

    private Map<String, Object> getPlatforms() {
        var archs = new HashMap<String, Object>();
        for (var a : getOptions().getArchitectures())
            archs.put(String.valueOf(a), "");
        if (archs.isEmpty())
            archs.put("amd64", "");
        return archs;
    }

    /**
     * Return list of the chisel slices
     */
    private String getProjectDeps() {
        var buffer = new StringBuffer();
        for (var dep : getOptions().getSlices()) {
            if (!buffer.isEmpty())
                buffer.append(" ");
            buffer.append(dep);
        }
        return buffer.toString();
    }

    /**
     * Get copy commands for the project output
     * cp foo.jar ${CRAFT_PART_INSTALL}/jars
     * cp bar.jar ${CRAFT_PART_INSTALL}/jars
     *
     * @return
     */
    private String getProjectCopyOutput(List<String> relativeJars) {
        var buffer = new StringBuffer();
        buffer.append("mkdir -p ${CRAFT_PART_INSTALL}/jars\n");
        for (var jar : relativeJars) {
            buffer.append(String.format("cp %s ${CRAFT_PART_INSTALL}/jars\n", jar));
        }

        return buffer.toString();
    }

    private Map<String, Object> getProjectParts(List<File> files, List<String> relativeJars) {
        IRuntimeProvider provider = getOptions().getJlink() ? new JLinkRuntimePart(getOptions()) : new RawRuntimePart(getOptions());
        var parts = new HashMap<String, Object>();
        parts.put("gradle/rockcraft/dump", getDumpPart(relativeJars));
        parts.put("gradle/rockcraft/runtime", provider.getRuntimePart(files));
        parts.put("gradle/rockcraft/deps", getDepsPart());
        return parts;
    }

    private Map<String, Object> getDumpPart(List<String> relativeJars) {
        var part = new HashMap<String, Object>();
        part.put("source", ".");
        part.put("plugin", "nil");
        part.put("override-build", getProjectCopyOutput(relativeJars));
        return part;
    }

    private Map<String, Object> getDepsPart() {
        var part = new HashMap<String, Object>();
        part.put("plugin", "nil");
        if (getOptions().getSource() != null) {
            part.put("source", getOptions().getSource());
            part.put("source-type", "git");
        }
        if (getOptions().getBranch() != null) {
            part.put("branch", getOptions().getBranch());
        }

        var overrideCommands = new StringBuffer();
        overrideCommands.append("chisel cut --release ./ --root ${CRAFT_PART_INSTALL} libc6_libs \\\n");
        overrideCommands.append(" libgcc-s1_libs \\\n");
        overrideCommands.append(" libstdc++6_libs \\\n");
        overrideCommands.append(" zlib1g_libs \\\n");
        overrideCommands.append(" base-files_base \\\n");
        if (getProjectDeps() != null) {
            overrideCommands.append(getProjectDeps());
            overrideCommands.append("\n");
        }
        overrideCommands.append("craftctl default");
        return part;
    }

    private Map<String, Object> getProjectService(List<String> relativeJars) {
        String command = getOptions().getCommand();
        var jarList = relativeJars.stream().filter(x -> !x.endsWith("-plain.jar")).toList();
        if (command == null || command.isBlank()) {
            if (jarList.size() == 1) {
                command = String.format("/usr/bin/java -jar %s", jarList.iterator().next());
            } else
                throw new UnsupportedOperationException("Rockcraft plugin requires either single jar output or command defined");
        }
        var serviceData = new HashMap<String, String>();
        serviceData.put("override", "replace");
        serviceData.put("summary", getOptions().getSummary());
        serviceData.put("startup", "enabled");
        serviceData.put("command", command);
        var services = new HashMap<String, Object>();
        services.put(getProject().getName(), serviceData);
        return services;
    }
}
