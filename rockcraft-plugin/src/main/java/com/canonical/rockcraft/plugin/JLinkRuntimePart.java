package com.canonical.rockcraft.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class JLinkRuntimePart implements IRuntimeProvider {

    private final RockcraftOptions options;

    public JLinkRuntimePart(RockcraftOptions options){
        this.options = options;
    }

    @Override
    public Map<String, Object> getRuntimePart(List<File> files) {
        var relativeJars = new ArrayList<String>();
        for (var jar : files) {
            relativeJars.add(String.format("jars/%s", jar.getName()));
        }
        var part = new HashMap<String, Object>();
        part.put("after", new String[]{"gradle/rockcraft/dump", "gradle/rockcraft/deps"});
        part.put("plugin", "jlink");
        part.put("jlink-jars", relativeJars);
        return part;
    }
}
