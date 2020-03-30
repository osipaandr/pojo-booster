package generator.db

interface DbTypeConverter {
    fun convert(sqlType: String): String
}
