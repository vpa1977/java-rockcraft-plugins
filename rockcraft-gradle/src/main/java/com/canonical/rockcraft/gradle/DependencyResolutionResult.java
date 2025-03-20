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
package com.canonical.rockcraft.gradle;

import org.gradle.api.artifacts.component.ComponentIdentifier;

import java.util.Set;

/**
 * Dependency lookup result for maven pom
 */
public class DependencyResolutionResult {
    private Set<ComponentIdentifier> dependencies;
    private Set<ComponentIdentifier> dependencyManagement;
    public DependencyResolutionResult(Set<ComponentIdentifier> dependencies, Set<ComponentIdentifier> dependencyManagement) {
        this.dependencies = dependencies;
        this.dependencyManagement = dependencyManagement;
    }

    public Set<ComponentIdentifier> getDependencies() { return this.dependencies; }
    public Set<ComponentIdentifier> getDependencyManagement() { return this.dependencyManagement; }
}
