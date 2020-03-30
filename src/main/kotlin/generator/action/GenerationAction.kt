package generator.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.impl.file.PsiDirectoryFactory
import generator.db.ColumnToFieldDefinitionMapper
import generator.db.findDefinitions
import generator.db.postgres.PostgresDefMapper
import generator.generation.PojoGenerator

class GenerationAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val tableName = "sample_table"
        // todo: скрыть это всё от кнопки
        // todo: UI для выбора БД
        val colDefinitions = findDefinitions(tableName)
        val defMapper: ColumnToFieldDefinitionMapper = PostgresDefMapper
        val definitions = defMapper.map(colDefinitions)
        // todo: перенести это всё в приличное место
        val psiClass = PojoGenerator(e.project!!).generate(tableName, definitions)
        WriteCommandAction.runWriteCommandAction(e.project
        ) {
            // todo: спрашивать пользователя, куда сохранять
            val directory = e.getData(PlatformDataKeys.PROJECT_FILE_DIRECTORY) ?: return@runWriteCommandAction
            PsiDirectoryFactory.getInstance(e.project)
                .createDirectory(directory)
                .add(psiClass)
        }
    }
}
