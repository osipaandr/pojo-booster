package generator.generation

import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.search.GlobalSearchScope
import generator.FieldDefinition
import underscoreToCamel

class PojoGenerator(private val project: Project) {

    private val psiFacade = JavaPsiFacade.getInstance(project)
    private val elementFactory = psiFacade.elementFactory

    fun generate(tableName: String, definitions: List<FieldDefinition>): PsiClass {
        val className = underscoreToCamel(tableName, true)
        val psiClass = elementFactory.createClass(className)
        definitions.forEach { def ->
            val clazz = psiFacade.findClass(def.qualifiedType, GlobalSearchScope.allScope(project))
                ?: throw UnsupportedOperationException("Type \"${def.qualifiedType}\" not found")
            val type = elementFactory.createType(clazz)
            val field = elementFactory.createField(def.fieldName, type)
            psiClass.add(field)
        }
        return psiClass
    }
}
