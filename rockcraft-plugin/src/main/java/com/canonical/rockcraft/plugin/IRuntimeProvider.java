package com.canonical.rockcraft.plugin;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * An interface to generate Java runtime part code.
 * The implementation can either build a runtime image using jlink plugin,
 * run a shell script to do it or prepare the image with jpackage.
 */
public interface IRuntimeProvider {

    /**
     * Generate rockcraft Java runtime part code.
     * @param files - list of jar files to analyze
     * @return part code
     */
    Map<String, Object> getRuntimePart(List<File> files);
}
