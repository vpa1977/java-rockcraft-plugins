allprojects {
    version = "0.2.1"
    group = "io.github.rockcrafters"
    apply(plugin = "maven-publish")
    plugins.withType<MavenPublishPlugin>().configureEach {
        extensions.configure<PublishingExtension>() {
            repositories {
                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/vpa1977/java-rockcraft-plugins")
                    credentials {
                        username = project.findProperty("gpr.user") as String? ?: System.getenv("GHP_USERNAME")
                        password = project.findProperty("gpr.key") as String? ?: System.getenv("GHP_TOKEN")
                    }
                }
            }
        }
    }
}
