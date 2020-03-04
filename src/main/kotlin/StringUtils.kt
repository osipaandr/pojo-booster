import com.google.common.base.CaseFormat

fun camelToUpperUnderscore(fieldName: String): String =
    CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, fieldName)
