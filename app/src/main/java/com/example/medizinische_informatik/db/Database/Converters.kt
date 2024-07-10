package com.example.medizinische_informatik.db.Database

import androidx.room.TypeConverter
import java.util.Date

class Converters {

    @TypeConverter
    fun fromDate(date : Date) : Long{
        return date.time
    }

    @TypeConverter
    fun toDate(time : Long) : Date{
        return Date(time)
    }

}