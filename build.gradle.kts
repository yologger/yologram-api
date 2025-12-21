import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

object DependencyVersions {
    const val TEST_CONTAINERS_VERSION = "1.19.5"
    const val QUERY_DSL_VERSION = "5.1.0"
    const val SPRING_CLOUD_AWS_VERSION = "3.1.0"
    const val SPRINGDOC_VERSION = "2.3.0"
    const val KOTLIN_LOGGING_VERSION = "6.0.3"
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
version = "0.0.1"

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
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.security:spring-security-crypto")

    // MySQL Driver
    runtimeOnly("com.mysql:mysql-connector-j")

    // Query DSL
    implementation("com.querydsl:querydsl-jpa:${DependencyVersions.QUERY_DSL_VERSION}:jakarta")
    kapt("com.querydsl:querydsl-apt:${DependencyVersions.QUERY_DSL_VERSION}:jakarta")

    // Spring Cloud AWS
    implementation(platform("io.awspring.cloud:spring-cloud-aws-dependencies:${DependencyVersions.SPRING_CLOUD_AWS_VERSION}"))
    implementation("io.awspring.cloud:spring-cloud-aws-starter-parameter-store")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.10.7")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.10.7")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.10.7")
    implementation("com.auth0:jwks-rsa:0.11.0")
    implementation("com.auth0:java-jwt:3.10.3")

    // Opensearch
    implementation("org.opensearch.client:opensearch-rest-high-level-client:2.15.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.18.0")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation(platform("org.testcontainers:testcontainers-bom:${DependencyVersions.TEST_CONTAINERS_VERSION}"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mysql")
    testImplementation("org.testcontainers:elasticsearch")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")

    // API Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${DependencyVersions.SPRINGDOC_VERSION}")

    if (isAppleSilicon()) {
        runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.94.Final:osx-aarch_64")
    }

    // Logging
    implementation("io.github.oshai:kotlin-logging-jvm:${DependencyVersions.KOTLIN_LOGGING_VERSION}")

    // Grafana Logging
    implementation("com.github.loki4j:loki-logback-appender:2.0.1")

    // Grafana Tracing & APM
    implementation("io.micrometer:micrometer-tracing-bridge-otel") // Micrometer와 OpenTelemetry를 연결하는 브리지
    implementation("io.opentelemetry:opentelemetry-exporter-otlp") // 수집한 trace를 Grafana Cloud로 전송하는 Exporter
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

fun isAppleSilicon(): Boolean =
    System.getProperty("os.name") == "Mac OS X" && System.getProperty("os.arch") == "aarch64"