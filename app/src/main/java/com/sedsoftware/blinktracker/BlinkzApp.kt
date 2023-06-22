package com.sedsoftware.blinktracker

import android.app.Application
import com.google.firebase.FirebaseApp

class BlinkzApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
