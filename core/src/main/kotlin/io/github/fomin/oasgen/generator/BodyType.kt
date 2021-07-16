package io.github.fomin.oasgen.generator

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType
import io.github.fomin.oasgen.MediaTypeObject

sealed class BodyType(val contentType: String) {
    class Binary(contentType: String) : BodyType(contentType)
    class Json(contentType: String, val jsonSchema: JsonSchema) : BodyType(contentType)

    fun jsonSchema(): JsonSchema? = if (this is Json) jsonSchema else null
}

fun bodyType(content: Map<String, MediaTypeObject>?): BodyType? {
    return if (content == null) {
        null
    } else {
        when (content.size) {
            0 -> null
            1 -> {
                val jsonMediaTypeObject = content["application/json"]
                if (jsonMediaTypeObject != null) {
                    BodyType.Json("application/json", jsonMediaTypeObject.schema())
                } else {
                    val entry = content.entries.first()
                    val mediaTypeObject = entry.value
                    val schema = mediaTypeObject.schema()
                    if (schema.type == JsonType.Scalar.STRING && schema.format == "binary") {
                        BodyType.Binary(entry.key)
                    } else {
                        error("expected binary or json response type ${mediaTypeObject.fragment}")
                    }
                }
            }
            else -> error("expected empty or single response type $content")
        }
    }
}
