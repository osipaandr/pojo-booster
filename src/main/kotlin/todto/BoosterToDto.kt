package todto

import AbstractPojoBooster
import camelToUpperUnderscore
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField

class BoosterToDto(event: AnActionEvent) : AbstractPojoBooster(event) {

    companion object {
        private val classAnnotations = arrayOf(
            // TODO: подумать, может, нужно что-то ещё
            "lombok.Data"
        )

        private const val jsonIgnore = "com.fasterxml.jackson.annotation.JsonIgnore"

        private fun jsonProperty(name: String) =
            "com.fasterxml.jackson.annotation.JsonProperty(\"${camelToUpperUnderscore(name)}\")"
    }

    override fun boost(psiClass: PsiClass) {
        val modifierList = psiClass.modifierList ?: return
        classAnnotations.forEach { modifierList.addAnnotationIfNecessary(it) }
        psiClass.fields.forEach(::processField)
        psiClass.methods.forEach { it.deleteIfAccessor() }
        psiClass.findIdField()?.let { psiClass.putFirst(it) }
    }

    private fun processField(field: PsiField) {
        val modifiers = field.withTypeChanged().modifierList
        modifiers?.let {
            if (!it.hasAnnotation(jsonIgnore)) {
                it.addAnnotationIfNecessary(jsonProperty(field.name))
            }
        }
    }
}
