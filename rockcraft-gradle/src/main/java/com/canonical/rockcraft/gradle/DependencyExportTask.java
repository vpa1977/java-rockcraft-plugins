/**
 * Copyright 2025 Canonical Ltd.
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
package com.canonical.rockcraft.gradle;

import com.canonical.rockcraft.builder.DependencyOptions;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.ArtifactCollection;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.result.ResolvedArtifactResult;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public abstract class DependencyExportTask extends DefaultTask {
    private final Logger logger = Logging.getLogger(DependencyExportTask.class);
    private final ArrayList<ModuleVersionIdentifier> workQueue = new ArrayList<>();
    private final DependencyOptions dependencyOptions;

    @Inject
    public DependencyExportTask(DependencyOptions options) {
        dependencyOptions = options;
    }

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory();

    @TaskAction
    public void export() throws IOException {
        for (String configName : dependencyOptions.getConfigurations()) {
            Configuration config = this.getProject().getConfigurations().findByName(configName);
            if (config == null) {
                throw new IllegalArgumentException(String.format("Configuration %s not found.", configName));
            }
            ArtifactCollection artifacts = config.getIncoming().getArtifacts();
            copyArtifacts(artifacts);
        }
        // export build script dependencies
        this.getProject().getBuildscript().getConfigurations().all(new Action<Configuration>() {
            @Override
            public void execute(Configuration files) {
                for (Dependency dep : files.getAllDependencies()) {
                    ArtifactCollection artifacts = getProject()
                            .getBuildscript()
                            .getConfigurations()
                            .detachedConfiguration(dep)
                            .getIncoming()
                            .getArtifacts();
                    try {
                        copyArtifacts(artifacts);
                    }
                    catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                }
            }
        });
    }

    private void copyArtifacts(ArtifactCollection artifacts) throws IOException {
        Path outputLocationRoot = getOutputDirectory().getAsFile().get().toPath();
        for (ResolvedArtifactResult result : artifacts.getArtifacts()) {
            copyFromGradleCache(result, outputLocationRoot);
        }
    }

    private void copyFromGradleCache(ResolvedArtifactResult resolvedArtifact, Path outputLocationRoot ) throws IOException {
        File f = resolvedArtifact.getFile();
        // gradle cache stores artifacts in <artifact>/<sha1>/<file> directory structure
        File componentLocation = f.getParentFile().getParentFile();
        String relativePath = resolvedArtifact.getId().getComponentIdentifier().getDisplayName().replace(':', File.separatorChar);
        Path outputLocation = outputLocationRoot.resolve(relativePath);
        for (File component : componentLocation.listFiles()) {
            Optional<File> file = Arrays.stream(component.listFiles()).findFirst();
            if (file.isEmpty())
                continue;
            outputLocation.toFile().mkdirs();
            Path output = outputLocation.resolve(file.get().getName());
            Files.copy(file.get().toPath(), output, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            Path outputSha1 = Path.of(output.toFile().getAbsolutePath() + ".sha1");
            String paddedSha1 = String.format("%40s", component.getName()).replace(' ', '0');
            Files.writeString(outputSha1, paddedSha1);
            logger.debug(String.format("Written %s and corresponding sha1", output));
        }
    }
}
