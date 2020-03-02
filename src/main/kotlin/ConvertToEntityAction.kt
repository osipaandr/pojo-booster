import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager

class ConvertToEntityAction : AnAction() {

    private val classAnnotations = arrayOf(
        "lombok.Accessors(chain = true)",
        "lombok.Getter",
        "lombok.Setter",
        "javax.persistence.Entity",
        "lombok.NoArgsConstructor"
    )

    override fun actionPerformed(e: AnActionEvent) {
        val virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return
        val project = e.project ?: return
        val psiFile = PsiManager.getInstance(project).findFile(virtualFile)

        val classes = (psiFile as PsiJavaFile).classes
        if (classes.size != 1) {
            return
        }
        val clazz = classes[0]
        WriteCommandAction.runWriteCommandAction(project) {
            val modifierList = clazz.modifierList ?: return@runWriteCommandAction
            classAnnotations.forEach { modifierList.addAnnotation(it) }
        }
    }
}