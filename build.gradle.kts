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

    /* ABI dependencies */

    //Web Connection Support
    implementation("com.neovisionaries:nv-websocket-client:2.9")
    implementation("com.squareup.okhttp3:okhttp:4.6.0")

    /* Internal dependencies */

    //General Utility
    implementation("net.sf.trove4j:trove4j:3.0.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.0")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
