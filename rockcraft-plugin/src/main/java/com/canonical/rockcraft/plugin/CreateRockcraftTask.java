package com.canonical.rockcraft.plugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.HashMap;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;

import org.apache.commons.text.StringSubstitutor;

public class CreateRockcraftTask extends DefaultTask {

    private static final String ROCKCRAFT_YAML = "rockcraft.yaml";

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

    @TaskAction
    public void writeRockcraft() throws IOException {
        SourceSet mainSourceSet = getProject().getExtensions().getByType(SourceSet.class);
        String mainOutput = mainSourceSet.getOutput().getAsPath();

        try (BufferedWriter wr = new BufferedWriter(new FileWriter(Path.of(mainOutput, ROCKCRAFT_YAML).toFile()))) {
            wr.write(createRockcraft());
        }

    }

    private String createRockcraft() throws IOException {
        var variables = new HashMap<String, String>();
        variables.put("project.name", getProject().getName());
        variables.put("project.version", String.valueOf(getProject().getVersion()));
        variables.put("project.summary", getProjectSummary());
        variables.put("project.description", getProjectDescription());
        variables.put("project.platforms", getProjectPlatforms());
        variables.put("project.service", getProjectService());
        variables.put("project.copyoutput", getProjectCopyOutput());
        variables.put("project.output", getProjectOutput());
        variables.put("project.output", getProjectDeps());

        return StringSubstitutor.replace(readTemplate(), variables);
    }

    /**
     * Return list of the chisel slices
     */
    private String getProjectDeps() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProjectDeps'");
    }

    /**
     * Get yaml list of the project output [ foo.jar, bar.jar ]
     * @return
     */
    private String getProjectOutput() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProjectOutput'");
    }

    /**
     * Get copy commands for the project output
     * cp foo.jar ${CRAFT_PART_INSTALL}/jars
     * cp bar.jar ${CRAFT_PART_INSTALL}/jars
     * @return
     */
    private String getProjectCopyOutput() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProjectCopyOutput'");
    }

    private String getProjectService() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProjectService'");
    }

    private String getProjectPlatforms() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProjectPlatforms'");
    }

    private String getProjectSummary() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProjectSummary'");
    }

    private String getProjectDescription() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProjectDescription'");
    }
}
