package generator.db

import generator.FieldDefinition
import generator.db.postgres.ColumnDefinition

interface ColumnToFieldDefinitionMapper {
    fun map(columnDefinitions: List<ColumnDefinition>): List<FieldDefinition>
}
