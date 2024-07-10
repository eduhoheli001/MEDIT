package com.example.medizinische_informatik

import android.app.Application
import androidx.room.Room
import com.example.medizinische_informatik.db.Database.HealthDB



class HealthApplication : Application() {

    companion object {
        lateinit var healthDatabase: HealthDB
    }

    override fun onCreate() {
        super.onCreate()
        healthDatabase = Room.databaseBuilder(
            applicationContext,
            HealthDB::class.java,
            HealthDB.NAME
        ).build()
    }
}