package io.github.fomin.oasgen.generator

import io.github.fomin.oasgen.Responses

fun response2xx(responses: Responses) =
    responses.byCode().entries.single { it.key.startsWith("2") }
