package booster.todto

import booster.common.AbstractBoostAction
import com.intellij.openapi.actionSystem.AnActionEvent

class BoostToDtoAction : AbstractBoostAction() {

    override fun pojoBooster(event: AnActionEvent) = BoosterToDto(event)
}
