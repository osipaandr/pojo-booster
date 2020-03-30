import com.google.common.base.CaseFormat
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

fun camelToUpperUnderscore(name: String): String =
    CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name)

fun underscoreToCamel(name: String, capitalized: Boolean = false): String {
    val targetFormat = if (capitalized) CaseFormat.UPPER_CAMEL else CaseFormat.LOWER_CAMEL
    return CaseFormat.UPPER_UNDERSCORE.to(targetFormat, name.toUpperCase())
}

fun showNotification(message: String, project: Project?, type: NotificationType = NotificationType.INFORMATION) {
    NotificationGroup("Pojo Booster", NotificationDisplayType.BALLOON, true)
        .createNotification(message, type)
        .notify(project)
}
