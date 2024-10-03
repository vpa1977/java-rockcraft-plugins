package com.canonical.rockcraft.gradle;

import com.canonical.rockcraft.builder.RockBuilder;
import com.canonical.rockcraft.builder.RockcraftOptions;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;

/**
 * This task pushes rock image to the local docker
 */
public class PushRockcraftTask extends AbstractRockcraftTask {

    /**
     * Constructs PushRockcraftTask
     *
     * @param options - rockcraft options
     */
    public PushRockcraftTask(RockcraftOptions options) {
        super(options);
    }

    /**
     * The task action
     *
     * @throws IOException          - IO error while writing <i>rockcraft.yaml</i>
     * @throws InterruptedException - <i>rockcraft</i> process was aborted
     */
    @TaskAction
    public void pushRock() throws IOException, InterruptedException {
        var settings = RockSettingsFactory.createRockProjectSettings(getProject());
        RockBuilder.buildRock(settings, getOptions());
    }
}
