pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "rockcraft-plugin"

include("rockcraft", "rockcraft-gradle", "rockcraft-maven")
