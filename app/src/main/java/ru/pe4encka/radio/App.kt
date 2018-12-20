package ru.pe4encka.radio

import android.app.Application

class App: Application(){
    override fun onCreate() {
        super.onCreate()
        Repository.init(applicationContext)
    }
}