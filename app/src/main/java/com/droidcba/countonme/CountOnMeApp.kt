package com.droidcba.countonme

import android.app.Application
import com.facebook.stetho.Stetho

/**
 *
 * @author juancho.
 */
class CountOnMeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this);
    }
}