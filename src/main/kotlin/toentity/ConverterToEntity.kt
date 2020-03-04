package toentity

import camelToUpperUnderscore
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope

class ConverterToEntity(event: AnActionEvent) {

    companion object {
        private val classAnnotations = arrayOf(
            "lombok.EqualsAndHashCode(callSuper = true)",
            "lombok.NoArgsConstructor",
            "javax.persistence.Entity",
            "lombok.Setter",
            "lombok.Getter",
            "lombok.Accessors(chain = true)"
        )

        private fun tableAnnotation(name: String): String =
            "javax.persistence.Table(name = \"$name\")"

        private fun columnAnnotation(fieldName: String): String =
            "javax.persistence.Column(name = \"$fieldName\")"
    }

    private val project = event.project!!
    private val timestampType: PsiClassType
    private val dateType: PsiClassType
    private val instantType: PsiClassType
    private val psiElementFactory: PsiElementFactory
    private val visitor: JavaElementVisitor

    init {
        class CustomVisitor : JavaElementVisitor() {
            override fun visitField(field: PsiField?) {
                super.visitField(field)
                processField(field!!)
            }

            override fun visitMethod(method: PsiMethod?) {
                super.visitMethod(method)
                method?.delete()
            }
        }
        visitor = CustomVisitor()

        val psiFacade = JavaPsiFacade.getInstance(project)
        psiElementFactory = psiFacade.elementFactory
        val findType: (String) -> PsiClassType =
            {
                val psiClass = psiFacade.findClass(it, GlobalSearchScope.allScope(project))!!
                psiElementFactory.createType(psiClass)
            }
        instantType = findType("java.time.Instant")
        timestampType = findType("java.sql.Timestamp")
        dateType = findType("java.sql.Date")
    }

    fun convert(virtualFile: VirtualFile) {
        val psiFile = PsiManager.getInstance(project).findFile(virtualFile)

        val classes = (psiFile as PsiJavaFile).classes
        if (classes.size != 1) {
            return
        }
        val clazz = classes[0]
        WriteCommandAction.runWriteCommandAction(project) {
            annotate(clazz)
            clazz.fields.forEach { it.accept(visitor) }
            clazz.methods.forEach { it.accept(visitor) }
        }

        // TODO: если есть поле pid или id, аннотировать @Id. Если есть оба, нихрена не делать
        //       если есть поле [имя_класса]_[id или pid], но нет id или pid, аннотировать его
    }

    private fun annotate(clazz: PsiClass) {
        val modifierList = clazz.modifierList!!
        val tableName = camelToUpperUnderscore(clazz.name!!)
        modifierList.addAnnotation(tableAnnotation(tableName))
        classAnnotations.forEach { modifierList.addAnnotation(it) }
    }

    private fun processField(field: PsiField) {
        val type = if (field.type == timestampType || field.type == dateType) instantType else field.type
        val annotationText = columnAnnotation(camelToUpperUnderscore(field.name))
        val newField = psiElementFactory.createField(field.name, type)
        newField.modifierList?.addAnnotation(annotationText)
        field.replace(newField)
    }
}