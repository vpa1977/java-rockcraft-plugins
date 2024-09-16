package com.canonical.rockcraft.plugin;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

public class RunimePartTest extends BaseRockcraftTest {
    @Test
    void rawRuntimePart() {
        RockcraftOptions options = new RockcraftOptions();
        RawRuntimePart part = new RawRuntimePart(options);
        Map<String, Object> code = part.getRuntimePart(Arrays.stream(new File[]{new File("/tmpfoo.jar")}).toList());
        assertEquals("nil", code.get("plugin"));
        var ret = (String[])code.get("build-packages");
        assertEquals("openjdk-21-jdk", ret[0]);
        assertTrue(code.get("override-build").toString().contains("--multi-release 21"));
    }

    @Test
    void rawRuntimeCustomOpenjdk() {
        RockcraftOptions options = new RockcraftOptions();
        options.setTargetRelease(8);
        options.setBuildPackage("openjdk-11-jdk");
        RawRuntimePart part = new RawRuntimePart(options);
        Map<String, Object> code = part.getRuntimePart(Arrays.stream(new File[]{new File("/tmpfoo.jar")}).toList());
        var ret = (String[])code.get("build-packages");
        assertEquals("openjdk-11-jdk", ret[0]);
        assertTrue(code.get("override-build").toString().contains("--multi-release 8"));
    }
}
