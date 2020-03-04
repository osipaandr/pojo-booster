package toentity

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys

class ConvertToEntityAction : AnAction() {

    // TODO: сделать использование ломбока опциональным
    // TODO: сделать меню настройки использования ломбока
    // TODO: проверить, можно ли плагином переключить настройки стиля
    //   (поставить спейсинг между полями = 1, потом реформатировать код и вернуть всё обратно)
    override fun actionPerformed(event: AnActionEvent) {
        // TODO: не VIRTUAL_FILE, а PSI_FILE
        val virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return
        ConverterToEntity(event).convert(virtualFile)
    }

}