allprojects {
    version = "0.2.1"
    group = "io.github.rockcrafters"
    apply(plugin = "maven-publish")
    plugins.withType<MavenPublishPlugin>().configureEach {
        extensions.configure<PublishingExtension>() {
            repositories {
                maven {
                    name = "GitHubPackages"
                    url = uri(System.getenv("GHP_REPO") ?: "")
                    credentials {
                        username = project.findProperty("gpr.user") as String? ?: System.getenv("GHP_USERNAME")
                        password = project.findProperty("gpr.key") as String? ?: System.getenv("GHP_TOKEN")
                    }
                }
            }
        }
    }
}
