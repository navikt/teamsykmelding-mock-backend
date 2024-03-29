package no.nav.syfo.legeerklaering.model

data class LegeerklaeringRequest(
    val fnr: String,
    val fnrLege: String,
    val diagnosekode: String,
    val diagnosekodesystem: String,
    val statusPresens: String?,
    val vedlegg: Boolean,
    val vedleggMedVirus: Boolean,
)
