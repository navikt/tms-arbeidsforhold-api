import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    kotlin("jvm").version(managed.versions.kotlin)

    alias(managed.plugins.tms.jarBundling)

    application
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://packages.confluent.io/maven")
    maven {
        url = uri("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
    }
    mavenLocal()
}

dependencies {
    implementation(managed.bundles.jackson)
    implementation(managed.bundles.ktorClient)
    implementation(managed.bundles.ktorServer)

    implementation(managed.kotlinLogging)
    implementation(managed.ktor.serialization.jackson)
    implementation(managed.ktor.server.cors)
    implementation(managed.ktor.server.defaultHeaders)
    implementation(managed.logback.encoder)
    implementation(managed.tms.ktorTokenSupport.userToken.exchange)
    implementation(managed.tms.ktorTokenSupport.userToken.verification)
    implementation(managed.tms.commonLib.metrics)
    implementation(managed.tms.commonLib.teamLogger)
    implementation(managed.tms.kafka.application)
    implementation(managed.prometheus.metric.core)

    testImplementation(managed.bundles.junit)
    testImplementation(managed.mockk)
    testImplementation(managed.bundles.kotest)
    testImplementation(managed.ktor.test.server)
    testImplementation(managed.ktor.test.clientMock)
    testImplementation(managed.tms.ktorTokenSupport.userToken.verificationMock)
}

application {
    mainClass.set("no.nav.tms.arbeidsforhold.api.ApplicationKt")
}

tasks {
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
            events("passed", "skipped", "failed")
        }
    }
}
