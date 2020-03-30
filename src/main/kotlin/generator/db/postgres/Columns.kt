package generator.db.postgres

import org.jetbrains.exposed.sql.Table

private const val DEFAULT_VARCHAR_LENGTH = 255

object Columns : Table("information_schema.COLUMNS") {
    val tblName = varchar("table_name", DEFAULT_VARCHAR_LENGTH)
    val columnName = varchar("column_name", DEFAULT_VARCHAR_LENGTH)
    val dataType = varchar("data_type", DEFAULT_VARCHAR_LENGTH)
    val varcharLength = integer("character_maximum_length")
}

data class ColumnDefinition(
    val columnName: String,
    val dataType: String,
    val varcharLength: Int?
)
