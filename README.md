# Rockcraft Gradle Plugin

Builds [Ubuntu ROCK](https://documentation.ubuntu.com/rockcraft/en/latest/explanation/rocks/) image for your application.
The plugin requires [rockcraft](https://github/canonical/rockcraft) installed.

![Github Actions](https://github.com/canonical/rockcraft-gradle-plugin/actions/workflows/build.yml/badge.svg)
[![GNU GPLv3 license](https://img.shields.io/badge/license-GPLv3-blue)](https://www.gnu.org/licenses/gpl-3.0.html#license-text)

# Getting started

Install rockcraft: `snap install rockcraft`.

To use the plugin, apply the following two steps:

### 1. Apply the plugin

##### Using the `plugins` DSL:

**Groovy**

    plugins {
        id 'com.canonical.rockcraft-plugin' version '0.0.1'
    }

**Kotlin**

    plugins {
        id("com.canonical.rockcraft-plugin") version "0.1.0"
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
            classpath 'com.canonical.rockcraft-plugin:0.0.1'
        }
    }
    apply plugin: 'com.canonical.rockcraft-plugin'

**Kotlin**

    buildscript {
        repositories {
            maven {
                setUrl("https://plugins.gradle.org/m2/")
            }
        }
        dependencies {
            classpath("com.canonical.rockcraft-plugin:0.0.1")
        }
    }
    apply(plugin = "com.canonical.rockcraft-plugin")


### 2. Configure ROCK container

The plugin allows setting up container summary and description,
target architectures and the startup service command line.

**Groovy**

    rockcraft {
        summary = 'A ROCK summary'
        description = 'README.md'
        command = '/usr/bin/java -jar jars/application.jar'
        source = 'http://github.com/myuser/chisel-releases'
        branch = 'my-chisel-release-branch'
        slices = ['busybox_bins', 'fontconfig_config']

    }

**Kotlin**

    rockcraft {
        summary = "A ROCK summary"
        description = "README.md"
        command = "/usr/bin/java -jar jars/application.jar"
        source = "http://github.com/myuser/chisel-releases"
        branch = "my-chisel-release-branch"
        slices("busybox_bins", "fontconfig_config")
    }

## Issues and Contributions

Issues can be reported to the [Issue tracker](https://github.com/canonical/rockcraft-gradle-plugin/issues/).

Contributions can be submitted via [Pull requests](https://github.com/canonical/rockcraft-gradle-plugin/pulls/),

# TODO

- Allow runtime package/version customisation.
- Allow custom rockcraft.yaml/snippets
- Error handling (empty jar file), no main class
