import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    java
    checkstyle
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
}

group = "com.ventionteams"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // JWT
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.6")

    // Utils
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    implementation("org.projectlombok:lombok")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    checkstyle("com.puppycrawl.tools:checkstyle:10.17.0")
    annotationProcessor("org.projectlombok:lombok")

    // DB
    implementation("org.liquibase:liquibase-core:4.28.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    // Testing
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Checkstyle> {
    isShowViolations = true
    maxWarnings = 0
    maxErrors = 0
    configFile = rootProject.file("config/checkstyle/checkstyle.xml")
}

tasks.withType<BootRun> {
    dependsOn(tasks.withType(Checkstyle::class))
}

tasks.withType<Test> {
    useJUnitPlatform()
}
