plugins {
    java
}

repositories {
    mavenCentral()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

dependencies {
    testImplementation("org.springframework:spring-webflux:7.0.10")
    testImplementation("tools.jackson.dataformat:jackson-dataformat-xml:3.1.8")
    testImplementation("com.squareup.okhttp3:mockwebserver3:5.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.0.3")
}

tasks.test {
    useJUnitPlatform()
}
