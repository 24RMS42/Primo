package com.primo.main

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.facebook.drawee.backends.pipeline.Fresco
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import io.realm.RealmConfiguration

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        MainClass.init(applicationContext)
        Fresco.initialize(this)
        val config = RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
        Fabric.with(this, Crashlytics());
    }
}