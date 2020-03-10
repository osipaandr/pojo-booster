package toentity

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys

class BoostToEntityAction : AnAction() {

    // TODO: сделать использование ломбока опциональным
    // TODO: сделать меню настройки использования ломбока
    // TODO: проверить, можно ли плагином переключить настройки стиля
    //   (поставить спейсинг между полями = 1, потом реформатировать код и вернуть всё обратно)
    override fun actionPerformed(event: AnActionEvent) {
        val psiFile = event.getData(PlatformDataKeys.PSI_FILE)
        psiFile?.let { BoosterToEntity(event).boost(it) }
    }
}
