import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

object DependencyVersions {
    const val TEST_CONTAINERS_VERSION = "1.19.5"
    const val QUERY_DSL_VERSION = "5.1.0"
    const val SPRING_CLOUD_AWS_VERSION = "3.1.0"
}

plugins {
    val kotlinVersion = "1.9.23"
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("kapt") version kotlinVersion
}

group = "link.yologram"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

tasks.getByName<Jar>("jar") {
    enabled = false
}

dependencies {
    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.security:spring-security-crypto")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // MySQL Driver
    runtimeOnly("com.mysql:mysql-connector-j")

    // Query DSL
    implementation("com.querydsl:querydsl-jpa:${DependencyVersions.QUERY_DSL_VERSION}:jakarta")
    kapt("com.querydsl:querydsl-apt:${DependencyVersions.QUERY_DSL_VERSION}:jakarta")

    // AWS SSM Parameters Store
    implementation(platform("io.awspring.cloud:spring-cloud-aws-dependencies:${DependencyVersions.SPRING_CLOUD_AWS_VERSION}"))
    implementation("io.awspring.cloud:spring-cloud-aws-starter-parameter-store")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.10.7")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.10.7")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.10.7")
    implementation("com.auth0:jwks-rsa:0.11.0")
    implementation("com.auth0:java-jwt:3.10.3")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation(platform("org.testcontainers:testcontainers-bom:${DependencyVersions.TEST_CONTAINERS_VERSION}"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mysql")
}

tasks.getByName<Jar>("jar") {
    enabled = false
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

when {
    project.hasProperty("prod") -> {
        println("Profile: prod")
        apply {
            from("profile_prod.gradle")
        }
    }

    project.hasProperty("staging") -> {
        println("Profile: staging")
        apply {
            from("profile_staging.gradle")
        }
    }

    else -> {
        println("Profile: dev")
        apply {
            from("profile_dev.gradle")
        }
    }
}