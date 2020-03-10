package toentity

import AbstractPojoBooster
import camelToUpperUnderscore
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField

class BoosterToEntity(event: AnActionEvent) : AbstractPojoBooster(event) {

    companion object {
        private val classAnnotations = arrayOf(
            "lombok.EqualsAndHashCode(callSuper = true)",
            "lombok.NoArgsConstructor",
            "javax.persistence.Entity",
            "lombok.Setter",
            "lombok.Getter",
            "lombok.Accessors(chain = true)"
        )

        private const val idAnnotation = "javax.persistence.Id"

        private fun tableAnnotation(name: String) =
            "javax.persistence.Table(name = \"$name\")"

        private fun columnAnnotation(fieldName: String) =
            "javax.persistence.Column(name = \"$fieldName\")"
    }

    override fun boost(psiClass: PsiClass) {
        annotateAsEntity(psiClass)
        psiClass.fields.forEach(::processField)
        psiClass.methods.forEach { it.deleteIfAccessor() }
        checkId(psiClass)?.let { psiClass.putFirst(it) }
    }

    private fun checkId(clazz: PsiClass) =
        clazz.findIdField()?.let { annotateAsId(it) }

    private fun annotateAsId(psiField: PsiField): PsiField {
        psiField.modifierList?.addAnnotationIfNecessary(idAnnotation)
        return psiField
    }

    private fun annotateAsEntity(clazz: PsiClass) {
        val modifierList = clazz.modifierList ?: return
        val tableName = camelToUpperUnderscore(clazz.name ?: return)
        with(modifierList) {
            addAnnotationIfNecessary(tableAnnotation(tableName))
            classAnnotations.forEach { addAnnotationIfNecessary(it) }
        }
    }

    private fun processField(field: PsiField) {
        val annotationText = columnAnnotation(camelToUpperUnderscore(field.name))
        field.withTypeChanged().modifierList
            ?.addAnnotationIfNecessary(annotationText)
    }
}
