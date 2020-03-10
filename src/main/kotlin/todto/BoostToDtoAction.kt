package todto

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys

class BoostToDtoAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val psiFile = event.getData(PlatformDataKeys.PSI_FILE)
        psiFile?.let { BoosterToDto(event).boost(it) }
    }
}
