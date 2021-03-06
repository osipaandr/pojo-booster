package common

import com.intellij.application.options.CodeStyle
import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.*
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import com.intellij.psi.search.GlobalSearchScope

abstract class AbstractPojoBooster(event: AnActionEvent) : PojoBooster {

    companion object {
        private const val ACCESSOR_PREFIX_LENGTH = 3
    }

    protected val project = event.project!!
    protected val timestampType: PsiClassType
    protected val dateType: PsiClassType
    protected val instantType: PsiClassType
    protected val psiElementFactory: PsiElementFactory

    init {
        val psiFacade = JavaPsiFacade.getInstance(project)
        psiElementFactory = psiFacade.elementFactory
        val findType: (String) -> PsiClassType =
            {
                val psiClass = psiFacade.findClass(it, GlobalSearchScope.allScope(project))
                    ?: throw ClassNotFoundException("PsiClass $it wasn't found")
                psiElementFactory.createType(psiClass)
            }
        instantType = findType("java.time.Instant")
        timestampType = findType("java.sql.Timestamp")
        dateType = findType("java.sql.Date")
    }

    override fun boost(psiFile: PsiFile) {
        if (psiFile !is PsiJavaFile) {
            return
        }
        if (psiFile.classes.size != 1) return
        WriteCommandAction.runWriteCommandAction(project) {
            val psiClass = psiFile.classes[0]
            boost(psiClass)
            createIntends(psiClass)
            JavaCodeStyleManager.getInstance(project)
                .shortenClassReferences(psiFile)
        }
    }

    private fun createIntends(psiClass: PsiClass) {
        val codeStyleSettings = CodeStyle.getSettings(project)
            .getCommonSettings(JavaLanguage.INSTANCE)
        val oldIntends = codeStyleSettings.BLANK_LINES_AROUND_FIELD
        codeStyleSettings.BLANK_LINES_AROUND_FIELD = 1
        CodeStyleManager.getInstance(project)
            .reformat(psiClass)
        codeStyleSettings.BLANK_LINES_AROUND_FIELD = oldIntends
    }

    protected abstract fun boost(psiClass: PsiClass)

    protected fun PsiField.withTypeChanged(): PsiField {
        if (type == timestampType || type == dateType) {
            val newField = psiElementFactory.createField(name, instantType)
            return replace(newField) as PsiField
        }
        return this
    }

    protected fun PsiModifierList.addAnnotationIfNecessary(annotationFullText: String): Boolean {
        val annotation = annotationFullText.takeWhile { it != '(' }
        if (hasAnnotation(annotation)) {
            return false
        }
        addAnnotation(annotationFullText)
        return true
    }

    protected fun PsiClass.putFirst(id: PsiField) {
        val indexToDrop = fields.indexOf(id) + 1
        addBefore(id, fields[0])
        fields[indexToDrop].delete()
    }

    protected fun PsiClass.findIdField(): PsiField? {
        val findField: (String) -> PsiField? = { findFieldByName(it, false) }
        val id = findField("id")
        val pid = findField("pid")
        return when {
            id != null && pid != null -> null
            id != null -> id
            pid != null -> pid
            else -> {
                val decapitalizedName = name?.decapitalize()
                decapitalizedName?.let { findField("${it}Id") ?: findField("${it}Pid") }
            }
        }
    }

    protected fun PsiMethod.isAccessor() =
        (name.startsWith("get") || name.startsWith("set")) &&
                containingClass?.findFieldByName(
                    name.substring(ACCESSOR_PREFIX_LENGTH).decapitalize(), true
                ) != null

    protected fun PsiMethod.deleteIfAccessor() {
        if (isAccessor()) delete()
    }
}
