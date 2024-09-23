plugins {
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.3.0"
}

group = "io.github.vpa1977"
version = "0.1.1"

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    implementation(libs.osdetector)
    implementation(libs.commons.text)
    implementation(libs.snakeyaml)

    // Use JUnit Jupiter for testing.
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

}

gradlePlugin {
    website = "https://github.com/canonical/rockcraft-gradle-plugin"
    vcsUrl = "https://github.com/canonical/rockcraft-gradle-plugin"

    plugins {
        create("rockcraftPlugin") {
            id = "io.github.vpa1977.rockcraft-plugin"
            displayName = "ROCK plugin"
            description = "Plugin for ROCK generation"
            implementationClass = "io.github.vpa1977.rockcraft.plugin.RockcraftPlugin"
            tags = listOf("rockcraft", "rock", "container", "docker", "oci")
        }
    }
}

tasks.named<Test>("test") {
    // Use JUnit Jupiter for unit tests.
    useJUnitPlatform()
}
