plugins {
    `java-library`
}

group = "me.syari.ss.discord"
version = "1.0"

repositories {
    jcenter()
}

dependencies {
    /* ABI dependencies */

    //Code safety
    implementation("org.jetbrains:annotations:19.0.0")

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
