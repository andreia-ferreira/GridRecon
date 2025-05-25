plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")

    // Apply the Application plugin to add support for building an executable JVM application.
    application
}

dependencies {
    implementation(project(":utils"))
    implementation(project(":domain"))
    implementation(project(":data"))
    testImplementation(kotlin("test"))
    testImplementation(libs.mockitoKotlin)
    implementation(libs.kotlinxCoroutines)
    testImplementation(libs.jupiterApi)
    testRuntimeOnly(libs.jupiterEngine)
}

application {
    // Define the Fully Qualified Name for the application main class
    // (Note that Kotlin compiles `App.kt` to a class with FQN `com.example.app.AppKt`.)
    mainClass = "net.penguin.app.AppKt"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "net.penguin.app.AppKt"
    }

    from({
        configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        }
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.test {
    useJUnitPlatform()
}

