package booster.toentity

import booster.common.AbstractBoostAction
import com.intellij.openapi.actionSystem.AnActionEvent

class BoostToEntityAction : AbstractBoostAction() {

    override fun pojoBooster(event: AnActionEvent) = BoosterToEntity(event)
}
