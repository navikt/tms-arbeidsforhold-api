import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    kotlin("jvm").version(Kotlin.version)

    id(TmsJarBundling.plugin)

    // Apply the application plugin to add support for building a CLI application.
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
    implementation(JacksonDatatype.datatypeJsr310)
    implementation(JacksonDatatype.moduleKotlin)
    implementation(KotlinLogging.logging)
    implementation(Ktor.Server.contentNegotiation)
    implementation(Ktor.Server.core)
    implementation(Ktor.Server.cors)
    implementation(Ktor.Server.defaultHeaders)
    implementation(Ktor.Server.netty)
    implementation(Ktor.Server.statusPages)
    implementation(Ktor.Server.auth)
    implementation(Ktor.Client.contentNegotiation)
    implementation(Ktor.Client.apache)
    implementation(Ktor.Serialization.jackson)
    implementation(Logstash.logbackEncoder)
    implementation(Postgresql.postgresql)
    implementation(TmsKtorTokenSupport.tokenXValidation)
    implementation(TmsKtorTokenSupport.tokendingsExchange)
    implementation(TmsKtorTokenSupport.idportenSidecar)
    implementation(TmsCommonLib.utils)
    implementation(TmsCommonLib.kubernetes)
    implementation(TmsCommonLib.observability)
    implementation(TmsCommonLib.metrics)
    implementation(TmsKafkaTools.kafkaApplication)
    implementation(Prometheus.metricsCore)

    testImplementation(JunitPlatform.launcher)
    testImplementation(JunitJupiter.api)
    testImplementation(Mockk.mockk)
    testImplementation(TestContainers.postgresql)
    testImplementation(Kotest.runnerJunit5)
    testImplementation(Kotest.assertionsCore)
    testImplementation(Ktor.Test.serverTestHost)
    testImplementation(Ktor.Test.clientMock)
    testImplementation(TmsKtorTokenSupport.tokenXValidationMock)
    testImplementation(TmsKtorTokenSupport.idportenSidecarMock)
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
