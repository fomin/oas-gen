package jsm

enum class ScalarFormat(val jsonType: JsonType.Scalar, val format: String?) {
    STRING(JsonType.Scalar.STRING, null),
    LOCAL_DATE_TIME(JsonType.Scalar.STRING, "local-date-time"),
    NUMBER(JsonType.Scalar.NUMBER, null),
    BOOLEAN(JsonType.Scalar.BOOLEAN, null),
    ;
}

fun getScalarFormat(jsonType: JsonType.Scalar, format: String?): ScalarFormat {
    return ScalarFormat.values()
            .find { it.jsonType == jsonType && it.format == format }
            ?: error("can't find scalar format for json type $jsonType and format $format")
}
