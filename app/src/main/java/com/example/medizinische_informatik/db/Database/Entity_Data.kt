package com.example.medizinische_informatik.db.Database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "authentication",indices = [Index(value = ["email"],unique = true)])
data class Authentication (
    var title : String,
    var vorname : String,
    var nachname : String,
    var email : String,//@ColumnInfo(name="email")
    var password : String,//@ColumnInfo(name="password")
    var geburtsdatum : String, //Todo: sollte Date sein!! geht aber nicht :c
    @PrimaryKey(autoGenerate = true)
    var id: Int =0,
)


@Entity(tableName = "physician", foreignKeys =[
    ForeignKey(
        entity= Authentication::class,
        parentColumns = ["id"],
        childColumns = ["authentication_id"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )])
data class Physician (
    var title : String,
    var vorname : String,
    var nachname : String,
    var email : String,
    var fachgebiet : String,
    var authentication_id : Int,
    @PrimaryKey(autoGenerate = true)
    var id: Int =0,
)

@Entity(tableName = "icd10", foreignKeys =[
    ForeignKey(
        entity= Authentication::class,
        parentColumns = ["id"],
        childColumns = ["authentication_id"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )])
data class ICD10 (
    //var code : String, könnte noch hinzugefügt werden
    //var freetext : String, könnte noch hinzugefügt werden
    var name : String,
    var authentication_id : Int,
    @PrimaryKey(autoGenerate = true)
    var id: Int =0,
)

@Entity(tableName = "allergies", foreignKeys =[
    ForeignKey(
        entity= Authentication::class,
        parentColumns = ["id"],
        childColumns = ["authentication_id"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )])
data class Allergies (
    //var freetext : String, könnte noch hinzugefügt werden
    var name : String,
    var authentication_id : Int,
    @PrimaryKey(autoGenerate = true)
    var id: Int =0,
)
@Entity(
    tableName = "mental_data",
    foreignKeys = [
        ForeignKey(
            entity = Authentication::class,
            parentColumns = ["id"],
            childColumns = ["authentication_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["authentication_id"])]
)
data class MentalData(
    var dailyWellBeingValue: Int,
    var painLevelValue: Int,
    var authentication_id: Int,
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
)
@Entity(
    tableName = "nutrition_data",
    foreignKeys = [
        ForeignKey(
            entity = Authentication::class,
            parentColumns = ["id"],
            childColumns = ["authentication_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["authentication_id"])]
)
data class NutritionData(
    var sugarConsumptionValue: Int,
    var alcoholConsumptionValue: Int,
    var meatConsumptionValue: Int,
    var eggConsumptionValue: Int,
    var milkConsumptionValue: Int,
    var caffeineConsumptionValue: Int,
    var authentication_id: Int,
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
)
@Entity(
    tableName = "sport_data",
    foreignKeys = [
        ForeignKey(
            entity = Authentication::class,
            parentColumns = ["id"],
            childColumns = ["authentication_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["authentication_id"])]
)
data class SportData(
    var sportTypeValue: String,
    var sportDurationValue: String,
    var sportIntensityValue: Int,
    var authentication_id: Int,
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
)
