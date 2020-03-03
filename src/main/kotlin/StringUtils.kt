// stringValue -> STRING_VALUE
// TODO: сделать правильно
fun toDatabaseFieldName(fieldName: String): String =
    Regex("[A-Z]").split(fieldName)
        .joinToString("_") { it.toUpperCase() }

