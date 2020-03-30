package generator.db.postgres

import generator.db.AbstractDbTypeConverter

object PostgresqlTypeConverter : AbstractDbTypeConverter() {
    override val typesMap: Map<String, String>
        get() = tMap

    private val tMap = initTypesMap()

    private fun initTypesMap(): Map<String, String> {
        return mapOf(
            Pair("bigint", LONG),
            Pair("int8", LONG),
            Pair("bigserial", LONG),
            Pair("serial8", LONG),
            Pair("boolean", BOOLEAN),
            Pair("bool", BOOLEAN),
            Pair("character", STRING),
            Pair("char", STRING),
            Pair("character varying", STRING),
            Pair("varchar", STRING),
            Pair("date", INSTANT),
            Pair("double precision", DOUBLE),
            Pair("float", DOUBLE),
            Pair("integer", LONG),
            Pair("int", LONG),
            Pair("int4", LONG),
            Pair("interval", INSTANT),
            Pair("numeric", LONG),
            Pair("decimal", LONG),
            Pair("real", DOUBLE),
            Pair("float4", DOUBLE),
            Pair("smallint", LONG),
            Pair("int2", LONG),
            Pair("text", STRING),
            Pair("time", INSTANT),
            Pair("timez", INSTANT),
            Pair("timestamp", INSTANT),
            Pair("timestampz", INSTANT)
        )
    }
}
