package com.devo.veclite

import android.app.Application
import android.os.Environment
import android.util.Log
import java.io.File

class VecliteApp : Application() {

    companion object {
        const val tag = "VecliteApp"

        lateinit var modelDir: File
        lateinit var app: Application
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        initModelDir()
    }

    private fun initModelDir() {
        val rootStorage = if (externalCacheDir == null) {
            Environment.getExternalStorageDirectory().absolutePath
        } else {
            externalCacheDir!!.absolutePath
        }
        modelDir = File(rootStorage, "llama").apply {
            if (!exists()) {
                mkdirs()
            }
        }

        Log.i(tag, "initModelDir")
    }

}