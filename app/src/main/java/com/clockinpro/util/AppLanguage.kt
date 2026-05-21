package com.clockinpro.util

enum class AppLanguage(
    val storageValue: String,
    val languageTags: String
) {
    SYSTEM("system", ""),
    ENGLISH("en", "en"),
    SIMPLIFIED_CHINESE("zh-CN", "zh-CN");

    companion object {
        fun fromStorageValue(value: String?): AppLanguage =
            values().firstOrNull { it.storageValue == value } ?: SYSTEM
    }
}
