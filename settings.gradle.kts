pluginManagement {
    repositories {
        gradlePluginPortal()
        maven(url="https://maven.pkg.github.com/rockcrafters/java-rockcraft-plugins")
    }
}

rootProject.name = "rockcraft-plugin"

include("rockcraft", "rockcraft-gradle")
