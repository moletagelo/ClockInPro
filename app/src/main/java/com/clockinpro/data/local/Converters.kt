package com.clockinpro.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromCheckType(value: CheckType): String = value.name

    @TypeConverter
    fun toCheckType(value: String): CheckType = CheckType.valueOf(value)

    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String = value.name

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus = SyncStatus.valueOf(value)

    @TypeConverter
    fun fromRepeatType(value: RepeatType): String = value.name

    @TypeConverter
    fun toRepeatType(value: String): RepeatType = RepeatType.valueOf(value)
}
