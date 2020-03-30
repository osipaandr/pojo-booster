package generator.db.postgres

import generator.FieldDefinition
import generator.db.ColumnToFieldDefinitionMapper
import underscoreToCamel

object PostgresDefMapper : ColumnToFieldDefinitionMapper {
    override fun map(columnDefinitions: List<ColumnDefinition>): List<FieldDefinition> =
        columnDefinitions.map {
            FieldDefinition(
                underscoreToCamel(it.columnName, capitalized = false),
                PostgresqlTypeConverter.convert(it.dataType)
            )
        }
}
