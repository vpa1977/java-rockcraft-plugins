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
package com.canonical.rockcraft.builder;

import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates a rockcraft.yaml based on RockOptions
 */
public class RockCrafter {

    private static final String ROCKCRAFT_YAML = "rockcraft.yaml";

    private final RockProjectSettings settings;
    private final RockcraftOptions options;
    private final File output;
    private final List<File> artifacts;

    /**
     * Creates RockCrafter
     * @param settings - Rockcraft project settins
     * @param options - Rockcraft creation options
     * @param output - output directory for rockcraft.yaml
     * @param artifacts - list of artifacts to package
     */
    public RockCrafter(RockProjectSettings settings, RockcraftOptions options, File output, List<File> artifacts) {
        this.settings = settings;
        this.options = options;
        this.output = output;
        this.artifacts = artifacts;
    }

    /**
     * Writes a rockcraft.yaml file to the output directory
     * @throws IOException - the method fails to write rockcraft.yaml
     */
    public void writeRockcraft() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(output, ROCKCRAFT_YAML)))) {
            String rockcraft = createRockcraft(output.toPath(), artifacts);
            writer.write(rockcraft);
        }
    }

    /**
     * Generate content of the <i>rockcraft.yaml</i>
     * @param root - location of build directory
     * @param files - list of .jar file artifacts
     * @return content of the <i>rockcraft.yaml</i>
     * @throws IOException - IO error writing <i>rockcraft.yaml</i>
     */
    protected String createRockcraft(Path root, List<File> files) throws IOException {
        files = files.stream().filter(x -> !x.getName().endsWith("-plain.jar")).toList();

        List<String> relativeJars = new ArrayList<String>();
        for (var file : files)
            relativeJars.add(root.relativize(file.toPath()).toString());

        var rockcraft = new HashMap<String, Object>();
        rockcraft.put("name", settings.getName());
        rockcraft.put("version", String.valueOf(settings.getVersion()));
        rockcraft.put("summary", getOptions().getSummary());
        var description = getOptions().getDescription();
        if (description != null) {
            var descriptionFile = settings.getProjectPath().resolve(description).toFile();
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
        var buffer = new StringBuilder();
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
        var buffer = new StringBuilder();
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
            part.put("source-branch", getOptions().getBranch());
        }

        String overrideCommands = "chisel cut ";
        if (getOptions().getSource() != null) {
            overrideCommands += "--release ./ ";
        }
        overrideCommands += """
                --root ${CRAFT_PART_INSTALL}/ libc6_libs \\
                    libgcc-s1_libs \\
                    libstdc++6_libs \\
                    zlib1g_libs \\
                    base-files_base \\
                    libnss3_libs """;

        if (getProjectDeps() != null) {
            overrideCommands += " " + getProjectDeps();
        }
        overrideCommands += "\ncraftctl default\n";
        part.put("override-build", overrideCommands);
        return part;
    }

    private Map<String, Object> getProjectService(List<String> relativeJars) {
        String command = getOptions().getCommand();
        if (command == null || command.isBlank()) {
            if (relativeJars.size() == 1) {

                command = String.format("/usr/bin/java -jar /jars/%s", Path.of(relativeJars.iterator().next()).getFileName().toString());
            } else {
                StringBuffer message = new StringBuffer();
                message.append("[ ");
                for (var entry : relativeJars) {
                    message.append(entry);
                    message.append(" ");
                }
                message.append("]");
                throw new UnsupportedOperationException("Rockcraft plugin requires either single jar output or a command defined: " + message);
            }
        }
        var serviceData = new HashMap<String, String>();
        serviceData.put("override", "replace");
        serviceData.put("summary", getOptions().getSummary());
        serviceData.put("startup", "enabled");
        serviceData.put("command", command);
        var services = new HashMap<String, Object>();
        services.put(settings.getName(), serviceData);
        return services;
    }

    private RockcraftOptions getOptions() {
        return options;
    }
}
