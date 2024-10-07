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
package com.canonical.rockcraft.maven;

import com.canonical.rockcraft.builder.RockcraftOptions;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.ArrayList;
import java.util.List;

public class AbstractRockMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "buildPackage")
    private String buildPackage = "openjdk-21-jdk";

    @Parameter(property = "targetRelease")
    private int targetRelease = 21;

    @Parameter(property = "jlink")
    private boolean jlink = false;

    @Parameter(property = "summary")
    private String summary = "";

    @Parameter(property = "description")
    private String description = null;

    @Parameter(property = "command")
    private String command = "";

    @Parameter(property = "source")
    private String source;

    @Parameter(property = "branch")
    private String branch;

    @Parameter(property = "architectures")
    private RockcraftOptions.RockArchitecture[] architectures = new RockcraftOptions.RockArchitecture[0];

    @Parameter(property = "slices")
    private List<String> slices = new ArrayList<String>();

    @Parameter(property = "docker")
    private boolean docker = false;

    private RockcraftOptions options = new RockcraftOptions();

    protected RockcraftOptions getOptions() {
        return options;
    }

    protected MavenProject getProject() {
        return project;
    }

    @Override
    public void execute() throws MojoExecutionException {
        options.setBuildPackage(buildPackage);
        options.setTargetRelease(targetRelease);
        options.setJlink(jlink);
        options.setSummary(summary);
        options.setDescription(description);
        options.setCommand(command);
        options.setSource(source);
        options.setBranch(branch);
        options.setArchitectures(architectures);
        options.setSlices(slices);
    }
}
