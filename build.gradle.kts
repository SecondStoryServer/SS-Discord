plugins {
    kotlin("jvm") version "1.3.72"
}

group = "me.syari.ss.discord"
version = "1.0"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.squareup.okhttp3:okhttp:4.7.1")
    implementation("com.google.code.gson:gson:2.8.0")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
