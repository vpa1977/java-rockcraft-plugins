/*
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
package com.canonical.rockcraft.builder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractRockCrafter {
    protected final RockProjectSettings settings;
    protected final CommonRockcraftOptions options;
    protected final List<File> artifacts;

    public AbstractRockCrafter(RockProjectSettings settings, CommonRockcraftOptions options, List<File> artifacts) {
        this.settings = settings;
        this.options = options;
        this.artifacts = artifacts;
    }

    protected List<File> getArtifacts() { return artifacts; }

    protected RockProjectSettings getSettings() { return settings; }

    public abstract void writeRockcraft() throws IOException;

    protected Map<String, Object> getPlatforms() {
        HashMap<String, Object> arches = new HashMap<String, Object>();
        for (RockArchitecture a : getOptions().getArchitectures())
            arches.put(String.valueOf(a), "");
        if (arches.isEmpty())
            arches.put("amd64", "");
        return arches;
    }

    protected CommonRockcraftOptions getOptions() {
        return options;
    }
}
