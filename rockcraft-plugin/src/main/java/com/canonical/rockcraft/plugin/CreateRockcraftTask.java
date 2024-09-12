package com.canonical.rockcraft.plugin;

import org.apache.commons.text.StringSubstitutor;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class CreateRockcraftTask extends DefaultTask {

    private static final String ROCKCRAFT_YAML = "rockcraft.yaml";

    private final RockcraftOptions options;

    private String readTemplate() throws IOException {
        try (var templateReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(ROCKCRAFT_YAML)))) {
            var buffer = new StringBuffer();
            String line;
            while ( (line = templateReader.readLine())!= null) {
                buffer.append(line);
                buffer.append("\n");
            }
            return buffer.toString();
        }
    }

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
        var variables = new HashMap<String, String>();
        variables.put("name", getProject().getName());
        variables.put("version", String.valueOf(getProject().getVersion()));
        variables.put("summary", getProjectSummary());
        variables.put("description", getProjectDescription());
        variables.put("platforms", getProjectPlatforms());
        variables.put("service", getProjectService(relativeJars));
        variables.put("copyoutput", getProjectCopyOutput(relativeJars));
        variables.put("output", getProjectOutput(files));
        variables.put("deps", getProjectDeps());
        variables.put("deps-source", getOptions().getDepsource());

        return StringSubstitutor.replace(readTemplate(), variables);
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
     * Get yaml list of the project output
     * @return [ jars/foo.jar, jars/bar.jar ]
     */
    private String getProjectOutput(Set<File> outputFiles) {
        var buffer = new StringBuffer();
        for (var file : outputFiles) {
            if (!buffer.isEmpty())
                buffer.append(", ");
            buffer.append(String.format("jars/{}", file.getName()));
        }
        return String.format("[ {} ]", buffer.toString().trim());
    }

    /**
     * Get copy commands for the project output
     * cp foo.jar ${CRAFT_PART_INSTALL}/jars
     * cp bar.jar ${CRAFT_PART_INSTALL}/jars
     * @return
     */
    private String getProjectCopyOutput(Set<String> relativeJars) {
        var buffer = new StringBuffer();
        for (var jar : relativeJars) {
            buffer.append(String.format("cp {} ${CRAFT_PART_INSTALL}/jars", jar));
        }
        return buffer.toString();
    }

    private String getProjectService(Set<String> relativeJars) {
        String command = getOptions().getCommand();
        var jarList = relativeJars.stream().filter( x -> !x.endsWith("-plain.jar")).toList();
        if (command == null || command.isBlank()) {
            if (jarList.size() == 1) {
                command = String.format("/usr/bin/java -jar {}", jarList.iterator().next());
            } else
                throw new UnsupportedOperationException("Rockcraft plugin requires either single jar output or command defined");
        }

        return String.format("""
                services:
                  {}:
                    override: replace
                    summary: {}
                    startup: enabled
                    command: {}
                """, getProject().getName(), getOptions().getSummary(), command);
    }

    private String getProjectPlatforms() {
        if (getOptions().getArchitectures().isEmpty())
            return "  amd:";

        var buffer = new StringBuffer();
        for (var item : getOptions().getArchitectures()) {
            buffer.append(String.format("  {}", item));
        }
        return buffer.toString();
    }

    private String getProjectSummary() {
        return getOptions().getSummary();
    }

    private String getProjectDescription() {
        return getOptions().getDescription();
    }
}
