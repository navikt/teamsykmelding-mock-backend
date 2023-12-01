group = "no.nav.syfo"
version = "1.0.0"

val coroutinesVersion = "1.7.3"
val jacksonVersion = "2.16.0"
val kluentVersion = "1.73"
val ktorVersion = "2.3.6"
val logbackVersion = "1.4.13"
val logstashEncoderVersion = "7.4"
val prometheusVersion = "0.16.0"
val smCommonVersion = "2.0.6"
val mockkVersion = "1.13.8"
val testContainerKafkaVersion = "1.19.3"
val kotlinVersion = "1.9.21"
val jaxbRuntimeVersion = "2.4.0-b180830.0438"
val jaxbApiVersion = "2.4.0-b180830.0359"
val sysfoXmlCodeGen = "2.0.1"
val javaTimeAdapterVersion = "1.1.3"
val commonsCodecVersion = "1.16.0"
val ktfmtVersion = "0.44"
val javaVersion = JavaVersion.VERSION_21
val junitJupiterVersion = "5.10.1"


plugins {
    id("application")
    id("com.diffplug.spotless") version "6.23.2"
    kotlin("jvm") version "1.9.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

application {
    mainClass.set("no.nav.syfo.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
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
    implementation("io.ktor:ktor-server-swagger:$ktorVersion")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    constraints {
        implementation("commons-codec:commons-codec:$commonsCodecVersion"){
            because("override transient version from io.ktor:ktor-client-apache")
        }
    }

    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashEncoderVersion")

    implementation("no.nav.helse:syfosm-common-kafka:$smCommonVersion")
    implementation("no.nav.helse:syfosm-common-mq:$smCommonVersion")
    implementation("no.nav.helse:syfosm-common-diagnosis-codes:$smCommonVersion")
    implementation("no.nav.helse:syfosm-common-models:$smCommonVersion")

    implementation("no.nav.helse.xml:xmlfellesformat:$sysfoXmlCodeGen")
    implementation("no.nav.helse.xml:kith-hodemelding:$sysfoXmlCodeGen")
    implementation("no.nav.helse.xml:sm2013:$sysfoXmlCodeGen")
    implementation("no.nav.helse.xml:legeerklaering:$sysfoXmlCodeGen")
    implementation("no.nav.helse.xml:papirsykemelding:$sysfoXmlCodeGen")
    implementation("javax.xml.bind:jaxb-api:$jaxbApiVersion")
    implementation("org.glassfish.jaxb:jaxb-runtime:$jaxbRuntimeVersion")
    implementation("com.migesok:jaxb-java-time-adapters:$javaTimeAdapterVersion")

    implementation("com.fasterxml.jackson.module:jackson-module-jaxb-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    testImplementation("org.amshove.kluent:kluent:$kluentVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.testcontainers:kafka:$testContainerKafkaVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion") {
        exclude(group = "org.eclipse.jetty")
    }
    testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {

compileKotlin {
        kotlinOptions.jvmTarget = javaVersion.toString()
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = javaVersion.toString()
    }
    shadowJar {
        archiveBaseName.set("app")
        archiveClassifier.set("")
        isZip64 = true
        manifest {
            attributes(
                mapOf(
                    "Main-Class" to "no.nav.syfo.ApplicationKt",
                ),
            )
        }
    }


    test {
        useJUnitPlatform {}
        testLogging {
            events("skipped", "failed")
            showStackTraces = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }


    spotless {
        kotlin { ktfmt(ktfmtVersion).kotlinlangStyle() }
        check {
            dependsOn("spotlessApply")
        }
    }
}
