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

        private const val idAnnotation = "javax.persistence.Id"

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
        visitor = object : JavaElementVisitor() {
            override fun visitField(field: PsiField?) {
                super.visitField(field)
                processField(field ?: return)
            }

            override fun visitMethod(method: PsiMethod?) {
                super.visitMethod(method)
                method?.delete()
            }
        }

        val psiFacade = JavaPsiFacade.getInstance(project)
        psiElementFactory = psiFacade.elementFactory
        val findType: (String) -> PsiClassType =
            {
                val psiClass = psiFacade.findClass(it, GlobalSearchScope.allScope(project))
                    ?: throw ClassNotFoundException("")
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
            // TODO: если нужные аннотации уже есть, то оставить в покое
            annotateAsEntity(clazz)
            clazz.fields.forEach { it.accept(visitor) }
            clazz.methods.forEach { it.accept(visitor) }
            checkId(clazz)
            // TODO: если есть айдишник, то сделать его первым полем
        }
    }

    private fun checkId(clazz: PsiClass) {
        val findField: (String) -> PsiField? = { clazz.findFieldByName(it, false) }
        val id = findField("id")
        val pid = findField("pid")
        when {
            (id != null && pid != null) -> return
            (id != null) -> annotateAsId(id)
            (pid != null) -> annotateAsId(pid)
            else -> {
                val className = clazz.name?.decapitalize() ?: return
                val namedId = findField("${className}Id") ?: findField("${className}Pid") ?: return
                annotateAsId(namedId)
            }
        }
    }

    private fun annotateAsId(psiField: PsiField) {
        psiField.modifierList?.addAnnotation(idAnnotation)
    }

    private fun annotateAsEntity(clazz: PsiClass) {
        val modifierList = clazz.modifierList ?: return
        val tableName = camelToUpperUnderscore(clazz.name ?: return)
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