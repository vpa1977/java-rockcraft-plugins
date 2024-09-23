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
        part.put("build-packages", new String[]{options.getBuildPackage()});
        part.put("multi-release", options.getTargetRelease());
        part.put("after", new String[]{"gradle/rockcraft/dump", "gradle/rockcraft/deps"});
        part.put("plugin", "jlink");
        part.put("jlink-jars", relativeJars);
        return part;
    }
}
