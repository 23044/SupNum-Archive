pluginManagement {
    repositories {
        google {
            content {
                // Use proper regex syntax for group inclusion
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.example.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // Disallow repositories in build.gradle.kts
    repositories {
        google()    // For resolving Android dependencies
        mavenCentral()  // For resolving other open-source dependencies
    }
}

rootProject.name = "SupNum Archive" // Set the root project name
include(":app") // Include the app module in the project
