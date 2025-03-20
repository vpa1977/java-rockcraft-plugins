# Rockcraft Build Plugins

Builds [Ubuntu ROCK](https://documentation.ubuntu.com/rockcraft/en/latest/explanation/rocks/) image for your application using [Gradle](https://gradle.org/) or [Maven](https://maven.apache.org/).
The plugin requires [rockcraft](https://github.com/canonical/rockcraft) installed.

![Github Actions](https://github.com/rockcrafters/java-rockcraft-plugins/actions/workflows/build.yml/badge.svg)
[![GNU GPLv3 license](https://img.shields.io/badge/license-GPLv3-blue)](https://www.gnu.org/licenses/gpl-3.0.html#license-text)

# How it works

The build plugins generate `rockcraft.yaml` in the output directory and build a [rock](https://documentation.ubuntu.com/rockcraft/en/latest/explanation/rocks/) image for your application.

The plugins provide tasks/goals to build and deploy [rock](https://documentation.ubuntu.com/rockcraft/en/latest/explanation/rocks/) image.

The parts generated are prefixed with a build system name, e.g. `gradle` or `maven`.
The plugins create the following parts in `rockcraft.yaml`:
* `<build-system>/rockcraft/runtime`: e.g. `maven/rockcraft/runtime` or `gradle/rockcraft/runtime`. This part generates a Java runtime image for the application using `jlink`. The part finds all jar files in the target image `/jars` directory and generates a runtime image deployed in the `/usr/jvm/java-<version>-openjdk-<arch>/` directory. It creates a symlink to `java` executable in `/usr/bin/java`.
* `<build-system>/rockcraft/deps`: deploys openjdk runtime dependencies.
* `<build-system>/rockcraft/dump`: copies build artifact into target image's `/jars` directory.

The rock is built using the base `bare` image.

The generated `rockraft.yaml` can be overridden by providing `rockcraftYaml` configuration property to the plugin. The plugin merges the generated `rockcraft.yaml` and the override one.

# Configuration Options

|Name|Description|
|----|-----------|
|buildPackage| OpenJDK Ubuntu package used to create a runtime image, e.g. `openjdk-21-jdk-headless`|
|targetRelease| `--multi-release` option passed to `jlink` |
|summary| rock image summary, e.g. `Spring Boot Application` |
|description| path to the description file, e.g. `README.md` |
|command| command used for the startup service |
|source | Git URL of `chisel-releases` repository |
|branch| Git branch of `chisel-releases` repository
|architectures| list of the supported architectures, e.g. `amd64, arm64` |
|slices| list of additional [chisel](https://github.com/canonical/chisel) slices to install |
|rockcraftYaml| path to `rockcraft.yaml` with the overrides for the generated `rockraft.yaml`
|createService| create startup service (default true) |

# Gradle Plugin

## Getting started

Install rockcraft: `snap install rockcraft`.

To use the plugin, apply the following two steps:

### 1. Apply the plugin

##### Using the `plugins` DSL:

**Groovy**

    plugins {
        id 'io.github.rockcrafters.rockcraft' version '1.0.0'
    }

**Kotlin**

    plugins {
        id("io.github.rockcrafters.rockcraft") version "1.0.0"
    }

##### Alternatively, you can use the `buildscript` DSL:

**Groovy**

    buildscript {
        repositories {
            maven {
                url "https://plugins.gradle.org/m2/"
            }
        }
        dependencies {
            classpath 'io.github.rockcrafters.rockcraft:1.0.0'
        }
    }
    apply plugin: 'io.github.rockcrafters.rockcraft-plugin'

**Kotlin**

    buildscript {
        repositories {
            maven {
                setUrl("https://plugins.gradle.org/m2/")
            }
        }
        dependencies {
            classpath("io.github.rockcrafters.rockcraft:1.0.0")
        }
    }
    apply(plugin = "io.github.rockcrafters.rockcraft")

### 2. Configure the container

The plugin allows setting up container summary and description,
target architectures and the startup service command line.

**Groovy**

    rockcraft {
        buildPackage = 'openjdk-21-jdk'
        targetRelease = 21
        summary = 'A ROCK summary'
        description = 'README.md'
        command = '/usr/bin/java -jar jars/application.jar'
        source = 'http://github.com/myuser/chisel-releases'
        branch = 'my-chisel-release-branch'
        slices = ['busybox_bins', 'fontconfig_config']
        architectures = ['amd64', 'arm64']
        createService = false
        rockcraftYaml = 'rockcraft.yaml'
    }

**Kotlin**

    rockcraft {
        buildPackage = "openjdk-21-jdk"
        targetRelease = 21
        summary = "A ROCK summary"
        description = "README.md"
        command = "/usr/bin/java -jar jars/application.jar"
        source = "http://github.com/myuser/chisel-releases"
        branch = "my-chisel-release-branch"
        slices("busybox_bins", "fontconfig_config")
        architectures("amd64", "arm64")
        createService = false
        rockcraftYaml =  "rockcraft.yaml"
    }

## Examples

Please see [examples](examples) to try the sample projects.

# Maven Plugin

## Getting started

Install rockcraft: `snap install rockcraft`.

To use the plugin, apply the following two steps:

### 1. Apply the plugin

Apply the plugin:

```xml
    <build>
        <plugins>
            ...
            <plugin>
                <groupId>io.github.rockcrafters</groupId>
                <artifactId>rockcraft-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <goals>
                            <!-- creates rockcraft.yaml -->
                            <goal>create-rock</goal>
                            <!-- builds rock image -->
                            <goal>build-rock</goal>
                            <!-- pushes rock to the local docker daemon-->
                            <goal>push-rock</goal>
                        </goals>
                    </execution>
                </executions>
            <plugin>
        </plugins>
    <build>
```
### 2. Configure the container

The plugin supports all [configuration options](#configuration-options).

```xml
        <plugins>
            <plugin>
                <groupId>io.github.rockcrafters</groupId>
                <artifactId>rockcraft-maven-plugin</artifactId>
                ...
                <configuration>
                    <buildPackage>openjdk-17-jdk-headless</buildPackage>
                    <targetRelease>17</targetRelease>
                    <jlink>false</jlink>
                    <summary>foo</summary>
                    <description>readme.txt</description>
                    <source>https://github.com/canonical/chisel-releases</source>
                    <branch>ubuntu-24.04</branch>
                    <architectures>
                        <architecture>amd64</architecture>
                        <architecture>arm64</architecture>
                    </architectures>
                    <slices>
                        <slice>busybox_bins</slice>
                        <slice>dash_bins</slice>
                    </slices>
                </configuration>
            </plugin>
```

## Examples

Please see [examples](examples) to try the sample projects.

# Issues and Contributions

Issues can be reported to the [Issue tracker](https://github.com/canonical/rockcraft-gradle-plugin/issues/).

Contributions can be submitted via [Pull requests](https://github.com/canonical/rockcraft-gradle-plugin/pulls/),

# TODO

- Error handling (empty jar file), no main class
