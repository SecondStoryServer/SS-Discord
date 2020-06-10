plugins {
    kotlin("jvm") version "1.3.72"
    id("net.minecrell.plugin-yml.bukkit") version "0.3.0"
    `maven-publish`
}

group = "me.syari.ss.discord"
version = "1.0.1"

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
    implementation("com.destroystokyo.paper:paper-api:1.15.2-R0.1-SNAPSHOT")
    implementation("me.syari.ss.core:SS-Core:3.0")
    implementation("me.syari.discord:KtDiscord:1.0")
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
    apiVersion = "1.15"
}

val jar by tasks.getting(Jar::class) {
    from(configurations.compileOnly.get().map {
        if (it.isDirectory) it else zipTree(it)
    })
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