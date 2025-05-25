plugins {
    kotlin("jvm")
}

group = "net.penguin"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinxCoroutinesTest)
    implementation(libs.kotlinxCoroutines)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}