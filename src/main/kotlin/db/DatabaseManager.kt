package db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseManager {

    fun doSmth() {
        val db = Database.connect(
            "jdbc:postgresql://localhost:5432/postgres",
            driver = "org.postgresql.Driver",
            user = "postgres", password = "2404"
        )

        val cols = transaction {
            Columns.select { Columns.tblName eq "sample_table" }
                .map { it.toColDef() }
                .toList()
        }
        println(cols)
    }

    private fun ResultRow.toColDef(): ColumnDefinition? {
        return ColumnDefinition(
            this[Columns.columnName],
            this[Columns.dataType],
            this[Columns.varcharLength]
        )
    }
}
