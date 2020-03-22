package settings

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.options.Configurable
import ui.SettingsForm
import javax.swing.JComponent

object SettingsManager : Configurable {

    private const val USE_LOMBOK = "USE_LOMBOK"
    private var settingsForm: SettingsForm? = null
    private var storedProperties = PropertiesComponent.getInstance()

    override fun isModified(): Boolean {
        return settingsForm?.useLombok() != getLombokUsage()
    }

    override fun getDisplayName() = "POJO Booster"

    override fun apply() {
        settingsForm?.let { storedProperties.setValue(USE_LOMBOK, it.useLombok()) }
    }

    override fun createComponent(): JComponent? {
        settingsForm = SettingsForm(getLombokUsage())
        return settingsForm?.settingsPanel
    }

    fun getLombokUsage() = storedProperties.getBoolean(USE_LOMBOK)
}
