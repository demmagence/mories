package com.demmagence.mories

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import java.io.File

@HiltAndroidApp
class MoriesApp : Application() {
    override fun onCreate() {
        // Catch all uncaught exceptions and save them to a file in cache directory
        // MUST BE SET BEFORE super.onCreate() so Hilt/Room startup errors are captured!
        val oldHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val file = File(cacheDir, "crash_log.txt")
                file.writeText(throwable.stackTraceToString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (oldHandler != null) {
                oldHandler.uncaughtException(thread, throwable)
            } else {
                android.os.Process.killProcess(android.os.Process.myPid())
                java.lang.System.exit(10)
            }
        }
        super.onCreate()
    }
}
