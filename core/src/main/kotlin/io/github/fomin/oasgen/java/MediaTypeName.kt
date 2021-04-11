package io.github.fomin.oasgen.java

val mediaType = mapOf("application/json" to "MediaType.APPLICATION_JSON",
        "application/octet-stream" to "MediaType.APPLICATION_OCTET_STREAM",
        "application/pdf" to "MediaType.APPLICATION_PDF",
        "multipart/form-data" to "MediaType.MULTIPART_FORM_DATA"
)

fun toMediaType(contentType: String) = mediaType[contentType]
