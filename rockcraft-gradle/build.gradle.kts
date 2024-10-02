plugins {
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.3.0"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    implementation(project(":rockcraft"))
    implementation(libs.osdetector)

    // Use JUnit Jupiter for testing.
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.snakeyaml)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

gradlePlugin {
    website = "https://github.com/canonical/rockcraft-gradle-plugin"
    vcsUrl = "https://github.com/canonical/rockcraft-gradle-plugin"

    plugins {
        create("rockcraftPlugin") {
            id = "com.canonical.rockcraft"
            displayName = "Rockcraft plugin"
            description = "Plugin for rock image generation"
            implementationClass = "com.canonical.rockcraft.gradle.RockcraftPlugin"
            tags = listOf("rockcraft", "rock", "container", "docker", "oci")
        }
    }
}

tasks.named<Test>("test") {
    // Use JUnit Jupiter for unit tests.
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    val compilerArgs = options.compilerArgs
    compilerArgs.add("-Xlint:all")
}
