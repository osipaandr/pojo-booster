package toentity

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys

class BoostToEntityAction : AnAction() {

    // TODO: сделать использование ломбока опциональным
    // TODO: сделать меню настройки использования ломбока
    override fun actionPerformed(event: AnActionEvent) {
        val psiFile = event.getData(PlatformDataKeys.PSI_FILE)
        psiFile?.let { BoosterToEntity(event).boost(it) }
    }
}
