package generator.db

import generator.db.postgres.ColumnDefinition
import generator.db.postgres.Columns
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

// todo: разобраться, надо ли делать disconnect вручную. Если да, то добавить код
fun findDefinitions(tableName: String): List<ColumnDefinition> {
    Database.connect(
        "jdbc:postgresql://localhost:5432/postgres",
        driver = "org.postgresql.Driver",
        user = "postgres", password = "2404"
    )
    return transaction {
        Columns.select { Columns.tblName eq tableName }
            .map { it.toColDef() }
            .toList()
    }
}

private fun ResultRow.toColDef(): ColumnDefinition {
    return ColumnDefinition(
        this[Columns.columnName],
        this[Columns.dataType],
        this[Columns.varcharLength]
    )
}
