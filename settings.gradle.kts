plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "score-board"
include("score-provider-lib")
include("score-board-app")
