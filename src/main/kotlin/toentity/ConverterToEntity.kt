package toentity

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import toDatabaseFieldName

class ConverterToEntity(event: AnActionEvent) {

    companion object {
        private val classAnnotations = arrayOf(
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
    private val instantType: PsiClassType
    private val psiElementFactory: PsiElementFactory

    init {
        val psiFacade = JavaPsiFacade.getInstance(project)
        psiElementFactory = psiFacade.elementFactory
        val findType: (String) -> PsiClassType =
            {
                val psiClass = psiFacade.findClass(it, GlobalSearchScope.allScope(project))!!
                psiElementFactory.createType(psiClass)
            }
        instantType = findType("java.time.Instant")
        timestampType = findType("java.sql.Timestamp")
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
            visitFieldsAndMethods(clazz)
        }

        // TODO: если есть поле pid или id, аннотировать @Id. Если есть оба, нихрена не делать
        //       если есть поле [имя_класса]_[id или pid], но нет id или pid, аннотировать его
        // TODO: сделать использование ломбока опциональным
        // TODO: сделать меню настройки использования ломбока
    }

    private fun visitFieldsAndMethods(clazz: PsiClass) {
        // TODO: эта херня реально почему-то не делает траверс, а останавливается на первом элементе. Исправить.
        class MyVisitor : JavaRecursiveElementVisitor() {
            override fun visitField(field: PsiField?) {
                super.visitField(field)
                processField(field!!)
            }
            override fun visitMethod(method: PsiMethod?) {
                super.visitMethod(method)
                method?.delete()
            }
        }
        // TODO: задать Владу вопрос про это гавно
        clazz.accept(MyVisitor())
    }

    private fun annotate(clazz: PsiClass) {
        val modifierList = clazz.modifierList!!
        // TODO: конвертировать имя нормально
        modifierList.addAnnotation(tableAnnotation(clazz.name!!))
        classAnnotations.forEach { modifierList.addAnnotation(it) }
    }

    private fun processField(field: PsiField) {
        // Аналогично для java.sql.Date
        val type = if (field.type == timestampType) instantType else field.type
        val annotationText = columnAnnotation(toDatabaseFieldName(field.name))
        val newField = psiElementFactory.createField(field.name, type)
        newField.modifierList?.addAnnotation(annotationText)
        field.replace(newField)
    }
}