plugins {
    kotlin("jvm") version "1.4.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.3.0"
    `maven-publish`
}

group = "me.syari.ss.discord"
version = "2.0.2"

val ssMavenRepoURL: String by extra
val ssMavenRepoUploadURL: String by extra
val ssMavenRepoUploadUser: String by extra
val ssMavenRepoUploadPassword: String by extra

repositories {
    mavenCentral()
    jcenter()
    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        url = uri(ssMavenRepoURL)
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.destroystokyo.paper:paper-api:1.16.2-R0.1-SNAPSHOT")
    implementation("me.syari.ss.core:SS-Core:3.1.1")
    compileOnly("me.syari.discord:KtDiscord:1.0.1") {
        exclude("org.jetbrains.kotlin")
        exclude("org.slf4j")
        exclude("com.google.code.gson")
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

bukkit {
    name = project.name
    version = project.version.toString()
    main = "$group.Main"
    author = "sya_ri"
    depend = listOf("SS-Core")
    apiVersion = "1.16"
}

val jar by tasks.getting(Jar::class)

val fatJar by tasks.registering(Jar::class) {
    archiveFileName.set("${project.name}-${project.version}-fat.jar")
    from(configurations.compileOnly.get().map { if (it.isDirectory) it else zipTree(it) })
    with(jar)
}

val sourceJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allJava.srcDirs)
}

publishing {
    repositories {
        maven {
            url = uri(ssMavenRepoUploadURL)
            credentials {
                username = ssMavenRepoUploadUser
                password = ssMavenRepoUploadPassword
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(sourceJar.get())
        }
    }
}