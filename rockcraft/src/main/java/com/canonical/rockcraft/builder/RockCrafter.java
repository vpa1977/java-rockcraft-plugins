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

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates a rockcraft.yaml based on RockOptions
 */
public class RockCrafter {

    private final RockProjectSettings settings;
    private final RockcraftOptions options;
    private final List<File> artifacts;

    /**
     * Creates RockCrafter
     *
     * @param settings  - Rockcraft project settins
     * @param options   - Rockcraft creation options
     * @param artifacts - list of artifacts to package
     */
    public RockCrafter(RockProjectSettings settings, RockcraftOptions options, List<File> artifacts) {
        this.settings = settings;
        this.options = options;
        this.artifacts = artifacts;
    }

    /**
     * Writes a rockcraft.yaml file to the output directory
     *
     * @throws IOException - the method fails to write rockcraft.yaml
     */
    public void writeRockcraft() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(settings.getRockOutput().resolve(IRockcraftNames.ROCKCRAFT_YAML).toFile()))) {
            String rockcraft = createRockcraft(settings.getRockOutput(), artifacts);
            writer.write(rockcraft);
        }
    }

    /**
     * Generate content of the <i>rockcraft.yaml</i>
     *
     * @param root  - location of build directory
     * @param files - list of .jar file artifacts
     * @return content of the <i>rockcraft.yaml</i>
     * @throws IOException - IO error writing <i>rockcraft.yaml</i>
     */
    protected String createRockcraft(Path root, List<File> files) throws IOException {
        ArrayList<File> filtered = new ArrayList<File>();
        for (File file : files) {
            if (file.getName().endsWith("-plain.jar"))
                continue;
            filtered.add(file);
        }
        List<String> relativeJars = new ArrayList<String>();
        for (File file : filtered) {
            relativeJars.add(root.relativize(file.toPath()).toString());
        }


        HashMap<String, Object> rockcraft = new HashMap<String, Object>();
        rockcraft.put(IRockcraftNames.ROCKCRAFT_NAME, settings.getName());
        rockcraft.put(IRockcraftNames.ROCKCRAFT_VERSION, String.valueOf(settings.getVersion()));
        rockcraft.put("summary", getOptions().getSummary());
        Path description = getOptions().getDescription();
        if (description != null) {
            File descriptionFile = settings.getProjectPath().resolve(description).toFile();
            if (!descriptionFile.exists())
                throw new UnsupportedOperationException("Rockcraft plugin description file does not exist.");
            rockcraft.put("description", new String(Files.readAllBytes(descriptionFile.toPath())));
        } else {
            rockcraft.put("description", "");
        }

        rockcraft.put("platforms", getPlatforms());
        rockcraft.put("base", "bare");
        rockcraft.put("build-base", "ubuntu@24.04");

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);

        StringBuilder yamlOutput = new StringBuilder();
        yamlOutput.append(yaml.dump(rockcraft));
        yamlOutput.append("\n");
        rockcraft.clear();

        rockcraft.put("services", getProjectService(relativeJars));
        yamlOutput.append(yaml.dump(rockcraft));
        yamlOutput.append("\n");
        rockcraft.clear();

        rockcraft.put("parts", getProjectParts(filtered, relativeJars));
        yamlOutput.append(yaml.dump(rockcraft));
        yamlOutput.append("\n");
        rockcraft.clear();

        return yamlOutput.toString();
    }

    private Map<String, Object> getPlatforms() {
        HashMap<String, Object> archs = new HashMap<String, Object>();
        for (RockcraftOptions.RockArchitecture a : getOptions().getArchitectures())
            archs.put(String.valueOf(a), "");
        if (archs.isEmpty())
            archs.put("amd64", "");
        return archs;
    }

    /**
     * Return list of the chisel slices
     */
    private String getProjectDeps() {
        StringBuilder buffer = new StringBuilder();
        for (String dep : getOptions().getSlices()) {
            if (buffer.length() > 0)
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
        StringBuilder buffer = new StringBuilder();
        buffer.append("mkdir -p ${CRAFT_PART_INSTALL}/jars\n");
        for (String jar : relativeJars) {
            buffer.append(String.format("cp %s ${CRAFT_PART_INSTALL}/jars\n", jar));
        }

        return buffer.toString();
    }

    private Map<String, Object> getProjectParts(List<File> files, List<String> relativeJars) {
        IRuntimeProvider provider = getOptions().getJlink() ? new JLinkRuntimePart(getOptions()) : new RawRuntimePart(getOptions());
        HashMap<String, Object> parts = new HashMap<String, Object>();
        parts.put(settings.getGeneratorName() + "/rockcraft/dump", getDumpPart(relativeJars));
        Map<java.lang.String,java.lang.Object> runtimePart = provider.getRuntimePart(files);
        runtimePart.put("after", new String[]{
                settings.getGeneratorName() + "/rockcraft/dump",
                settings.getGeneratorName() + "/rockcraft/deps"
        });
        parts.put(settings.getGeneratorName() + "/rockcraft/runtime", runtimePart);
        parts.put(settings.getGeneratorName() + "/rockcraft/deps", getDepsPart());
        return parts;
    }

    private Map<String, Object> getDumpPart(List<String> relativeJars) {
        HashMap<String, Object> part = new HashMap<String, Object>();
        part.put("source", ".");
        part.put("plugin", "nil");
        part.put("override-build", getProjectCopyOutput(relativeJars));
        return part;
    }

    private Map<String, Object> getDepsPart() {
        HashMap<String, Object> part = new HashMap<String, Object>();
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
        overrideCommands += " --root ${CRAFT_PART_INSTALL}/ libc6_libs \\\n";
        overrideCommands += " libgcc-s1_libs \\\n";
        overrideCommands += " libstdc++6_libs \\\n";
        overrideCommands += " zlib1g_libs base-files_base \\\n";
        overrideCommands += " libnss3_libs ";

        if (getProjectDeps() != null) {
            overrideCommands += " " + getProjectDeps();
        }
        overrideCommands += "\ncraftctl default\n";
        part.put("override-build", overrideCommands);
        return part;
    }

    private Map<String, Object> getProjectService(List<String> relativeJars) {
        String command = getOptions().getCommand();
        if (command == null || command.trim().isEmpty()) {
            if (relativeJars.size() == 1) {
                command = String.format("/usr/bin/java -jar /jars/%s", Paths.get(relativeJars.iterator().next()).getFileName().toString());
            } else {
                StringBuffer message = new StringBuffer();
                message.append("[ ");
                for (String entry : relativeJars) {
                    message.append(entry);
                    message.append(" ");
                }
                message.append("]");
                throw new UnsupportedOperationException("Rockcraft plugin requires either single jar output or a command defined: " + message);
            }
        }
        HashMap<String, String> serviceData = new HashMap<String, String>();
        serviceData.put("override", "replace");
        serviceData.put("summary", getOptions().getSummary());
        serviceData.put("startup", "enabled");
        serviceData.put("command", command);
        HashMap<String, Object> services = new HashMap<String, Object>();
        services.put(settings.getName(), serviceData);
        return services;
    }

    private RockcraftOptions getOptions() {
        return options;
    }
}
