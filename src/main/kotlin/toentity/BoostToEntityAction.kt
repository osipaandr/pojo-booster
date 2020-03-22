package toentity

import com.intellij.openapi.actionSystem.AnActionEvent
import common.AbstractBoostAction

class BoostToEntityAction : AbstractBoostAction() {

    override fun pojoBooster(event: AnActionEvent) = BoosterToEntity(event)
}
