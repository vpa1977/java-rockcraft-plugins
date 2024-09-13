package com.canonical.rockcraft.plugin;

import java.util.ArrayList;
import java.util.List;

public class RockcraftOptions {
    private boolean jlink = false;
    private String summary = "";
    private String description = "";
    private String command = "";
    private String source;
    private String branch;
    // @TODO: make architecture enumeration type-safe
    private List<String> architectures = new ArrayList<String>();
    private List<String> slices = new ArrayList<String>();

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
    public List<String> getArchitectures() {
        return architectures;
    }

    public void setArchitectures(List<String> architectures) {
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

    public void setSource(String depsource) {
        this.source = source;
    }
}
