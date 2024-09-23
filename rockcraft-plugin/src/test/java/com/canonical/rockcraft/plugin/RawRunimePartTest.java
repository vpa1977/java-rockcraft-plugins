/**
 * Copyright 2024 Canonical Ltd.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.canonical.rockcraft.plugin;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

public class RawRunimePartTest extends BaseRockcraftTest {
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
