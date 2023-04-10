plugins {
    java
    id("org.springframework.boot") version "3.0.5"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "kz.kaznu"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web:3.0.5")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-devtools:3.0.5")
    implementation("org.jsoup:jsoup:1.15.4")
    implementation("org.flywaydb:flyway-core")
    implementation("org.springframework.boot:spring-boot-starter-mail:3.0.5")

    implementation("commons-io:commons-io:2.11.0")
    implementation("org.apache.tika:tika-core:2.7.0")
    implementation("org.apache.tika:tika-parser-microsoft-module:2.7.0")
    implementation("org.apache.pdfbox:pdfbox:2.0.27")

    annotationProcessor("org.projectlombok:lombok:1.18.26")
    compileOnly("org.projectlombok:lombok:1.18.26")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
