package com.canonical.rockcraft.plugin;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    }
}
