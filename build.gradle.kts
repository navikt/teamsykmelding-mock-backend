import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "no.nav.syfo"
version = "1.0.0"

val coroutinesVersion = "1.6.2"
val jacksonVersion = "2.13.3"
val kluentVersion = "1.68"
val ktorVersion = "2.0.3"
val logbackVersion = "1.2.11"
val logstashEncoderVersion = "7.2"
val prometheusVersion = "0.15.0"
val smCommonVersion = "1.f132f2b"
val mockkVersion = "1.12.4"
val testContainerKafkaVersion = "1.17.2"
val kotlinVersion = "1.7.10"
val kotestVersion = "5.3.1"
val swaggerUiVersion = "4.11.1"
val fellesformatVersion = "1.e6fcef8"
val jaxbRuntimeVersion = "2.4.0-b180830.0438"
val jaxbApiVersion = "2.4.0-b180830.0359"
val legeerklaeringVersion = "2019.07.29-02-53-86b22e73f7843e422ee500b486dac387a582f2d1"
val papirsykmeldingVersion = "2019.09.09-08-50-693492ddc1d3f98e70c1638c94dcb95a66036d12"
val javaTimeAdapterVersion = "1.1.3"

tasks.withType<Jar> {
    manifest.attributes["Main-Class"] = "no.nav.syfo.BootstrapKt"
}

plugins {
    id("org.jmailen.kotlinter") version "3.10.0"
    kotlin("jvm") version "1.7.10"
    id("com.diffplug.spotless") version "6.5.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.hidetake.swagger.generator") version "2.18.2" apply true
    jacoco
}

buildscript {
    dependencies {
    }
}

val githubUser: String by project
val githubPassword: String by project

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/navikt/syfosm-common")
        credentials {
            username = githubUser
            password = githubPassword
        }
    }
}


dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$coroutinesVersion")
    implementation("io.prometheus:simpleclient_hotspot:$prometheusVersion")
    implementation("io.prometheus:simpleclient_common:$prometheusVersion")

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-call-id:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashEncoderVersion")

    implementation("no.nav.helse:syfosm-common-kafka:$smCommonVersion")
    implementation("no.nav.helse:syfosm-common-mq:$smCommonVersion")
    implementation("no.nav.helse:syfosm-common-diagnosis-codes:$smCommonVersion")
    implementation("no.nav.helse:syfosm-common-models:$smCommonVersion")

    implementation("no.nav.helse.xml:xmlfellesformat:$fellesformatVersion")
    implementation("no.nav.helse.xml:kith-hodemelding:$fellesformatVersion")
    implementation("no.nav.helse.xml:sm2013:$fellesformatVersion")
    implementation("no.nav.helse.xml:legeerklaering:$legeerklaeringVersion")
    implementation ("no.nav.helse.xml:papirSykemelding:$papirsykmeldingVersion")
    implementation("javax.xml.bind:jaxb-api:$jaxbApiVersion")
    implementation("org.glassfish.jaxb:jaxb-runtime:$jaxbRuntimeVersion")
    implementation("com.migesok:jaxb-java-time-adapters:$javaTimeAdapterVersion")

    implementation("com.fasterxml.jackson.module:jackson-module-jaxb-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    swaggerUI( "org.webjars:swagger-ui:$swaggerUiVersion")

    testImplementation("org.amshove.kluent:kluent:$kluentVersion") 
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.testcontainers:kafka:$testContainerKafkaVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion") {
        exclude(group = "org.eclipse.jetty") 
    }
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }
}

swaggerSources {
    create("teamsykmelding-mock-backend").apply {
        setInputFile(file("api/oas3/teamsykmelding-mock-backend-api.yaml"))
    }
}

tasks {

    create("printVersion") {
        println(project.version)
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    withType<JacocoReport> {
        classDirectories.setFrom(
                sourceSets.main.get().output.asFileTree.matching {
                    exclude()
                }
        )
    }

    withType<org.hidetake.gradle.swagger.generator.GenerateSwaggerUI> {
        outputDir = File(buildDir.path + "/resources/main/api")
    }

    withType<ShadowJar> {
        transform(ServiceFileTransformer::class.java) {
            setPath("META-INF/cxf")
            include("bus-extensions.txt")
        }
        dependsOn("generateSwaggerUI")
    }

    withType<Test> {
        useJUnitPlatform {
        }
        testLogging.showStandardStreams = true
    }

    "check" {
        dependsOn("formatKotlin")
    }
}
