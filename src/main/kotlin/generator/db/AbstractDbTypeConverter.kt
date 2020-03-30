package generator.db

abstract class AbstractDbTypeConverter : DbTypeConverter {
    companion object {
        const val LONG = "java.lang.Long"
        const val BOOLEAN = "java.lang.Boolean"
        const val STRING = "java.lang.String"
        const val INSTANT = "java.time.Instant"
        const val DOUBLE = "java.lang.Double"
        const val OBJECT = "java.lang.Object"
    }

    protected abstract val typesMap: Map<String, String>

    override fun convert(sqlType: String): String {
        return typesMap[sqlType] ?: OBJECT
    }
}
