package todto

import com.intellij.openapi.actionSystem.AnActionEvent
import common.AbstractBoostAction

class BoostToDtoAction : AbstractBoostAction() {

    override fun pojoBooster(event: AnActionEvent) = BoosterToDto(event)
}
