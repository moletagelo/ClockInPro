package com.clockinpro.data.local

import com.clockinpro.domain.model.CheckRecord as DomainCheckRecord
import com.clockinpro.domain.model.CheckType as DomainCheckType
import com.clockinpro.domain.model.SyncStatus as DomainSyncStatus

fun UserEntity.toDomain() = com.clockinpro.domain.model.User(
    id = id,
    phone = phone,
    passwordHash = passwordHash,
    nickname = nickname,
    avatarUrl = avatarUrl,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun com.clockinpro.domain.model.User.toEntity() = UserEntity(
    id = id,
    phone = phone,
    passwordHash = passwordHash,
    nickname = nickname,
    avatarUrl = avatarUrl,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun CheckRecordEntity.toDomain() = DomainCheckRecord(
    id = id,
    userId = userId,
    type = when (type) {
        CheckType.CHECK_IN -> DomainCheckType.CHECK_IN
        CheckType.CHECK_OUT -> DomainCheckType.CHECK_OUT
    },
    timestamp = timestamp,
    latitude = latitude,
    longitude = longitude,
    address = address,
    photoPath = photoPath,
    remark = remark,
    syncStatus = when (syncStatus) {
        SyncStatus.SYNCED -> DomainSyncStatus.SYNCED
        SyncStatus.PENDING -> DomainSyncStatus.PENDING
        SyncStatus.FAILED -> DomainSyncStatus.FAILED
    },
    createdAt = createdAt
)

fun DomainCheckRecord.toEntity() = CheckRecordEntity(
    id = id,
    userId = userId,
    type = when (type) {
        DomainCheckType.CHECK_IN -> CheckType.CHECK_IN
        DomainCheckType.CHECK_OUT -> CheckType.CHECK_OUT
    },
    timestamp = timestamp,
    latitude = latitude,
    longitude = longitude,
    address = address,
    photoPath = photoPath,
    remark = remark,
    syncStatus = when (syncStatus) {
        DomainSyncStatus.SYNCED -> SyncStatus.SYNCED
        DomainSyncStatus.PENDING -> SyncStatus.PENDING
        DomainSyncStatus.FAILED -> SyncStatus.FAILED
    },
    createdAt = createdAt
)
