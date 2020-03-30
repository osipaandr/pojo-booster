package booster.common

import com.intellij.psi.PsiFile

interface PojoBooster {
    fun boost(psiFile: PsiFile)
}
