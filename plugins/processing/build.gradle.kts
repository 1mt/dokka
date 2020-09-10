import org.jetbrains.registerDokkaArtifactPublication

registerDokkaArtifactPublication("dokkaAllModulesPage") {
    artifactId = "dokka-all-modules-page"
}

dependencies {
    api(project(":plugins:kotlin-analysis"))
    val coroutines_version: String by project
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
}