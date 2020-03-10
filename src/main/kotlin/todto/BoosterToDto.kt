package todto

import AbstractPojoBooster
import camelToUpperUnderscore
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod

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

    private val visitor = object : JavaElementVisitor() {
        override fun visitField(field: PsiField?) {
            super.visitField(field)
            field?.let { processField(it) }
        }

        override fun visitMethod(method: PsiMethod?) {
            super.visitMethod(method)
            // TODO: удалять только геттеры и сеттеры
            method?.delete()
        }
    }

    override fun boost(psiClass: PsiClass) {
        val modifierList = psiClass.modifierList ?: return
        classAnnotations.forEach { modifierList.addAnnotationIfNecessary(it) }
        psiClass.fields.forEach(visitor::visitField)
        psiClass.methods.forEach(visitor::visitMethod)
        psiClass.findIdField()?.let { psiClass.putFirst(it) }
    }

    private fun processField(field: PsiField) {
        field.withTypeChanged().modifierList?.let {
            if (!it.hasAnnotation(jsonIgnore)) {
                it.addAnnotationIfNecessary(jsonProperty(field.name))
            }
        }
    }
}
