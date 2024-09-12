package com.canonical.rockcraft.plugin;

import java.util.List;

public class RockcraftOptions {
    private String summary;
    private String description;
    private String command;
    // @TODO: make architecture enumeration type-safe
    private List<String> architectures;
    private List<String> slices;

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
}
