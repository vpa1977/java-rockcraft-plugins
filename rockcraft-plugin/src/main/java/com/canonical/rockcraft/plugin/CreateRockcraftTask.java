package com.canonical.rockcraft.plugin;

import org.apache.commons.text.StringSubstitutor;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.yaml.snakeyaml.Yaml;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
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
                    wr.write(createRockcraft(buildDir.getAsFile().get().toPath(), archives.getArtifacts().getFiles().getFiles()));
                }
            } catch (IOException e ) {
                throw new UnsupportedOperationException("Failed to write rockcraft.yaml: " + e.getMessage());
            }
        });
    }

    protected String createRockcraft(Path root, Set<File> files) throws IOException {
        Set<String> relativeJars = new HashSet<String>();
        for (var file : files)
            relativeJars.add(root.relativize(file.toPath()).toString());

        var rockcraft = new HashMap<String, Object>();
        rockcraft.put("name", getProject().getName());
        rockcraft.put("version", String.valueOf(getProject().getVersion()));
        rockcraft.put("summary", getOptions().getSummary());
        rockcraft.put("description", getOptions().getDescription());
        rockcraft.put("platforms", getOptions().getArchitectures());
        rockcraft.put("base", "bare");
        rockcraft.put("build-base", "ubuntu@24.04");
        rockcraft.put("services", getProjectService(relativeJars));
        rockcraft.put("parts", getProjectParts(files, relativeJars));
        return new Yaml().dump(rockcraft);
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
     * @return
     */
    private String getProjectCopyOutput(Set<String> relativeJars) {
        var buffer = new StringBuffer();
        buffer.append("mkdir -p ${CRAFT_PART_INSTALL}/jars\n");
        for (var jar : relativeJars) {
            buffer.append(String.format("cp {} ${CRAFT_PART_INSTALL}/jars\n", jar));
        }

        return buffer.toString();
    }

    private Map<String, Object> getProjectParts(Set<File> files, Set<String> relativeJars) {
        var parts = new HashMap<String, Object>();
        parts.put("gradle/rockcraft/dump", getDumpPart(relativeJars));
        parts.put("gradle/rockcraft/runtime", getRuntimePart(files));
        parts.put("gradle/rockcraft/deps", getDepsPart());
        return parts;
    }

    private Map<String, Object> getDumpPart(Set<String> relativeJars) {
        var part = new HashMap<String, Object>();
        part.put("source", ".");
        part.put("plugin", "nil");
        part.put("override-build", getProjectCopyOutput(relativeJars));
        return part;
    }

    private Map<String, Object> getRuntimePart(Set<File> jars) {
        var relativeJars = new ArrayList<String>();
        for (var jar : jars) {
            relativeJars.add(String.format("jars/{}", jar.getName()));
        }
        var part = new HashMap<String, Object>();
        part.put("after", new String[]{"gradle/rockcraft/dump", "gradle/rockcraft/deps"});
        part.put("plugin", "jlink");
        part.put("jlink-jars", relativeJars);
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
        part.put("override-build", String.format("""
                                        chisel cut --release ./ --root ${CRAFT_PART_INSTALL} libc6_libs \
                                        libgcc-s1_libs \
                                        libstdc++6_libs \
                                        zlib1g_libs \
                                        libnss3_libs \
                                        base-files_base {}
                                        craftctl default
            """, getProjectDeps()));
        return part;
    }

    private Map<String, Object> getProjectService(Set<String> relativeJars) {
        String command = getOptions().getCommand();
        var jarList = relativeJars.stream().filter( x -> !x.endsWith("-plain.jar")).toList();
        if (command == null || command.isBlank()) {
            if (jarList.size() == 1) {
                command = String.format("/usr/bin/java -jar {}", jarList.iterator().next());
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
