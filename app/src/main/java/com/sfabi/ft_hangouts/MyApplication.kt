package com.sfabi.ft_hangouts

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.Context

class MyApplication : Application(), Application.ActivityLifecycleCallbacks {

    private var startedActivityCount = 0
    private var lastStopTime: Long = 0L

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LanguageUtils.onAttach(base))
    }

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityStarted(activity: Activity) {
        if (startedActivityCount == 0) {
            if (lastStopTime != 0L) {
                val currentTime = System.currentTimeMillis()
                val diff = currentTime - lastStopTime

                val seconds = diff / 1000
                val millis = diff % 1000

                if (seconds < 1 && millis < 200) {
                    startedActivityCount++
                    return
                }

                val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val timeString = dateFormat.format(Date(lastStopTime))

                val message = activity.getString(R.string.toast_time) + " " + "$timeString ($seconds s e $millis ms)"

                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
        startedActivityCount++
    }

    override fun onActivityStopped(activity: Activity) {
        startedActivityCount--
        if (startedActivityCount == 0) {
            lastStopTime = System.currentTimeMillis()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}