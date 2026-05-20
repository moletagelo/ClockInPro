package com.clockinpro.v2.data.backup

import com.google.gson.GsonBuilder

object BackupCodec {
    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    fun encode(payload: BackupPayload): String = gson.toJson(payload)

    fun decode(json: String): BackupPayload = gson.fromJson(json, BackupPayload::class.java)
}
