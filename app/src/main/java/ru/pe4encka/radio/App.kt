package ru.pe4encka.radio

import android.app.Application
import android.util.Log
import ru.pe4encka.radio.database.Catalog
import ru.pe4encka.radio.repository.Repository

class App: Application(){
    override fun onCreate() {
        super.onCreate()

        Log.v("Application","OnCreate")

        Log.v("Application", "Init repository")
        Repository.init(applicationContext)
        Log.v("Application", "ok")
        Log.v("Application","Init catalog")
        Catalog.init(applicationContext)
        Log.v("Application","ok")
    }
}