package com.canonical.rockcraft.plugin;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface IRuntimeProvider {

    /**
     * Generate rockcraft Java runtime part code.
     * @param file - list of jar files to analyze
     * @return part code
     */
    Map<String, Object> getRuntimePart(List<File> files);
}
