package todto

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys

class BoostToDtoAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        event.getData(PlatformDataKeys.PSI_FILE)?.let {
            BoosterToDto(event).boost(it)
        }
    }
}
