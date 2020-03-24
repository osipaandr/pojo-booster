package common

import com.google.common.base.CaseFormat
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

fun camelToUpperUnderscore(fieldName: String): String =
    CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, fieldName)

fun showNotification(message: String, project: Project?, type: NotificationType = NotificationType.INFORMATION) {
    NotificationGroup("Pojo Booster", NotificationDisplayType.BALLOON, true)
        .createNotification(message, type)
        .notify(project)
}
