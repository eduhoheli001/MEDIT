package com.example.medizinische_informatik.db.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [
    Authentication::class,
    Physician::class,
    ICD10::class,
    Allergies::class,
    MentalData::class,
    NutritionData::class,
    SportData::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class HealthDB : RoomDatabase(){

    companion object {
        const val NAME = "TEST_NEU1"
    }

    abstract fun getAuthDao() : AuthDBDao

}