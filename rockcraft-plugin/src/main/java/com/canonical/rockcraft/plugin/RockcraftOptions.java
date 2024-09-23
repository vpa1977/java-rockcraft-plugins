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

import java.util.ArrayList;
import java.util.List;

public class RockcraftOptions {

    public enum RockArchitecture {
        amd64,
        arm64,
        armhf,
        i386,
        ppc64el,
        riscv64,
        s390x;
    }

    private String buildPackage = "openjdk-21-jdk";
    private int targetRelease = 21;
    private boolean jlink = false;
    private String summary = "";
    private String description = "";
    private String command = "";
    private String source;
    private String branch;
    private RockArchitecture[] architectures = new RockArchitecture[0];
    private List<String> slices = new ArrayList<String>();

    public int getTargetRelease() {
        return targetRelease;
    }

    public void setTargetRelease(int targetRelease) {
        this.targetRelease = targetRelease;
    }

    public String getBuildPackage() {
        return buildPackage;
    }

    public void setBuildPackage(String buildPackage) {
        this.buildPackage = buildPackage;
    }

    public boolean getJlink() {
        return jlink;
    }

    public void setJlink(boolean jlink) {
        this.jlink = jlink;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public RockArchitecture[] getArchitectures() {
        return architectures;
    }

    public void setArchitectures(RockArchitecture[] architectures) {
        this.architectures = architectures;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List<String> getSlices() {
        return slices;
    }

    public void setSlices(List<String> slices) {
        this.slices = slices;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
