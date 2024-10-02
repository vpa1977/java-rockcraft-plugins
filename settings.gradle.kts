pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "rockcraft-plugin"

include("rockcraft-build", "rockcraft-gradle", "rockcraft-maven")
