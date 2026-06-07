plugins {
    id("org.zaproxy.addon") version "0.11.0"
    id("java")
}

group = "org.zaproxy.addon.ollama"
version = "1.0.0"

repositories {
    mavenCentral()
}

zapAddOn {
    addOnName.set("Zap-o-Llama")
    addOnId.set("zap-ollama")
    addOnStatus.set("alpha")
    addOnVersion.set(version.toString())
    zapVersion.set("2.15.0")
}

dependencies {
    compileOnly("org.zaproxy:zap:2.15.0")
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}

tasks.test {
    useJUnitPlatform()
}
