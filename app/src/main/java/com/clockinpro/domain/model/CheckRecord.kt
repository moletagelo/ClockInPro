package com.clockinpro.domain.model

data class CheckRecord(
    val id: Long = 0,
    val userId: Long,
    val type: CheckType,
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null,
    val photoPath: String? = null,
    val remark: String? = null,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

enum class CheckType {
    CHECK_IN,
    CHECK_OUT
}

enum class SyncStatus {
    SYNCED,
    PENDING,
    FAILED
}
