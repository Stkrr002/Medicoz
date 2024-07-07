pluginManagement {
    repositories {
        maven {
            setUrl("https://github.com/jitsi/jitsi-maven-repository/raw/master/releases")
        }
        google()
        mavenCentral()
        maven("https://jitpack.io")
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            setUrl("https://github.com/jitsi/jitsi-maven-repository/raw/master/releases")
        }
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
rootProject.name = "Medico"
include(":app")