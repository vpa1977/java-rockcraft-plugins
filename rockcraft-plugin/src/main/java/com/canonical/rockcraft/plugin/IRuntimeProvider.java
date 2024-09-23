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
