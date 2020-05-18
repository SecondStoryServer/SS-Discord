plugins {
    signing
    `java-library`
    `maven-publish`

    id("com.jfrog.bintray") version "1.8.1"
    id("com.github.ben-manes.versions") version "0.19.0"
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

project.group = "net.dv8tion"
project.version = "1.0"
val archivesBaseName = "JDA"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    jcenter()
}

dependencies {
    /* ABI dependencies */

    //Code safety
    implementation("org.jetbrains:annotations:19.0.0")

    //Logger
    implementation("org.slf4j:slf4j-api:1.7.25")

    //Web Connection Support
    implementation("com.neovisionaries:nv-websocket-client:2.9")
    implementation("com.squareup.okhttp3:okhttp:4.6.0")


    //Collections Utility
    api("org.apache.commons:commons-collections4:4.4")


    /* Internal dependencies */

    //General Utility
    implementation("net.sf.trove4j:trove4j:3.0.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.0")
}
