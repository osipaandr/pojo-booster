package toentity

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys

class ConvertToEntityAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        // TODO: не VIRTUAL_FILE, а PSI_FILE
        val virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return
        ConverterToEntity(event).convert(virtualFile)
    }

}