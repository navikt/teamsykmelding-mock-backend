import no.nav.syfo.mq.MqClient
import no.nav.syfo.mq.MqClientDevelopment
import no.nav.syfo.pdl.client.DevelopmentPdlClient
import no.nav.syfo.pdl.client.PdlClient
import no.nav.syfo.pdl.service.PdlPersonService
import no.nav.syfo.sykmelding.SlettSykmeldingService
import no.nav.syfo.sykmelding.SykmeldingService
import no.nav.syfo.sykmelding.client.SyfosmregisterClient
import no.nav.syfo.sykmelding.client.SyfosmregisterClientDevelopment
import no.nav.syfo.sykmelding.client.SyfosmreglerClient
import no.nav.syfo.sykmelding.client.SyfosmreglerClientDevelopment
import no.nav.syfo.sykmelding.kafka.SykmeldingStatusKafkaProducer
import no.nav.syfo.sykmelding.kafka.SykmeldingStatusKafkaProducerDevelopment
import no.nav.syfo.sykmelding.kafka.TombstoneKafkaProducer
import no.nav.syfo.sykmelding.kafka.TombstoneKafkaProducerDevelopment
import no.nav.syfo.utils.EnvironmentVariables
import org.koin.core.KoinApplication
import org.koin.dsl.module
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("no.nav.syfo.sykmelding-syfosmregister")

fun KoinApplication.initDevelopmentModules() {
    modules(
        developmentEnv,
        developmentmqModule,
        developmentPdlModule,
        developmentSyfosmreglerModule,
        developmentSyfosmregisterModule,
        developmentKafkaModules,
        developmentSykmeldingModule,
    )
}

val developmentEnv = module {
    single {
        logger.info("initialiseing developmentenv")
        EnvironmentVariables(
            applicationPort = 8080,
            applicationName = "dummy-value",
            narmestelederTopic = "dummy-value",
            pdlScope = "dummy-value",
            pdlGraphqlPath = "dummy-value",
            aadAccessTokenUrl = "dummy-value",
            clientId = "dummy-value",
            clientSecret = "dummy-value",
            jwkKeysUrl = "dummy-value",
            jwtIssuer = "dummy-value",
            mqHostname = "dummy-value",
            mqPort = 1,
            mqGatewayName = "dummy-value",
            mqChannelName = "dummy-value",
            sykmeldingQueue = "dummy-value",
            legeerklaeringQueue = "dummy-value",
            dokarkivUrl = "dummy-value",
            dokarkivScope = "dummy-value",
            papirSmRegistreringTopic = "dummy-value",
            manuellTopic = "dummy-value",
            sykmeldingStatusTopic = "dummy-value",
            syfosmregisterUrl = "dummy-value",
            syfosmregisterScope = "dummy-value",
            syfosmreglerUrl = "dummy-value",
            syfosmreglerScope = "dummy-value",
            syfosmpapirreglerUrl = "dummy-value",
            syfosmpapirreglerScope = "dummy-value",
            norskHelsenettUrl = "dummy-value",
            norskHelsenettScope = "dummy-value",
            oppgaveUrl = "dummy-value",
            oppgaveScope = "dummy-value",
            clusterName = "dummy-value",
        )
    }
}

val developmentmqModule = module {
    single<MqClient> {
        MqClientDevelopment()
    }
}

val developmentPdlModule = module {
    single<PdlClient> {
        DevelopmentPdlClient()
    }
    single {
        val env = get<EnvironmentVariables>()
        PdlPersonService(get(), get(), env.pdlScope)
    }
}

val developmentSyfosmreglerModule = module {
    single<SyfosmreglerClient> { SyfosmreglerClientDevelopment() }
}

val developmentSyfosmregisterModule = module {
    single<SyfosmregisterClient> { SyfosmregisterClientDevelopment() }
}

val developmentKafkaModules = module {
    single<TombstoneKafkaProducer> {
        TombstoneKafkaProducerDevelopment()
    }
    single<SykmeldingStatusKafkaProducer> {
        SykmeldingStatusKafkaProducerDevelopment()
    }
}

val developmentSykmeldingModule = module {
    single {
        val env = get<EnvironmentVariables>()
        SykmeldingService(
            pdlPersonService = get(),
            mqClient = get(),
            sykmeldingQueue = env.sykmeldingQueue,
            syfosmreglerClient = get(),
        )
    }

    single {
        SlettSykmeldingService(
            syfosmregisterClient = get(),
            sykmeldingStatusKafkaProducer = get(),
            tombstoneKafkaProducer = get(),
        )
    }
}