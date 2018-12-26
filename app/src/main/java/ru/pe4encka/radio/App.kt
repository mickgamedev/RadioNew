package ru.pe4encka.radio

import android.app.Application
import ru.pe4encka.radio.database.Catalog
import ru.pe4encka.radio.repository.Repository

class App: Application(){
    override fun onCreate() {
        super.onCreate()

        Repository.init(applicationContext)
        Catalog.init(applicationContext)
    }
}