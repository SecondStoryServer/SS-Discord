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
    api("com.google.code.findbugs:jsr305:3.0.2")
    api("org.jetbrains:annotations:16.0.1")

    //Logger
    api("org.slf4j:slf4j-api:1.7.25")

    //Web Connection Support
    api("com.neovisionaries:nv-websocket-client:2.9")
    api("com.squareup.okhttp3:okhttp:3.13.0")

    //Opus library support
    api("club.minnced:opus-java:1.0.4@pom") {
        isTransitive = true
    }

    //Collections Utility
    api("org.apache.commons:commons-collections4:4.1")

    //we use this only together with opus-java
    // if that dependency is excluded it also doesn't need jna anymore
    // since jna is a transitive runtime dependency of opus-java we don't include it explicitly as dependency
    compileOnly("net.java.dev.jna:jna:4.4.0")

    /* Internal dependencies */

    //General Utility
    implementation("net.sf.trove4j:trove4j:3.0.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.1")

    //Sets the dependencies for the examples
    configurations.asMap["examplesCompile"] = configurations["apiElements"]
    configurations.asMap["examplesRuntime"] = configurations["implementation"]

    testImplementation("org.junit.jupiter:junit-jupiter:5.4.0")
}
