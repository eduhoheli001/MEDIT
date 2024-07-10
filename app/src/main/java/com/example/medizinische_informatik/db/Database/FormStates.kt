package com.example.medizinische_informatik.db.Database

import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItem(
    val id: Int,
    val tempId: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)
data class RegisterStates(
    val title: String = "",
    val vorname: String = "",
    val nachname: String = "",
    val geburtsdatum: String = "",
    val email: String = "",
    val password: String = "",
    val password2: String = "",
    val isAddingContact: Boolean = false,
    val id: Int = 0,
)

data class FormStates(
    val title: String = "",
    val vorname: String = "",
    val nachname: String = "",
    val geburtsdatum: String = "",
    val email: String = "",
    val password: String = "",
    val password2: String = "",
    val isAddingContact: Boolean = false,
    val id: Int = 0,
    )
data class FormPhysState(
    val title: String = "",
    val vorname: String = "",
    val nachname: String = "",
    val email: String="",
    val fachgebiet: String="",
    val isAddingContact: Boolean = false,
)
data class HealthFormState(
    var dailyWellBeingValue: Int = 3,
    var painLevelValue: Int = 0,
    var sugarConsumptionValue: Int = 0,
    var alcoholConsumptionValue: Int = 0,
    var meatConsumptionValue: Int = 0,
    var eggConsumptionValue: Int = 0,
    var milkConsumptionValue: Int = 0,
    var caffeineConsumptionValue: Int = 0,
    var sportTypeValue: String = "",
    var sportDurationValue: String = "",
    var sportIntensityValue: Int = 6,
    var checkSportActivityValue: Boolean = false,
    var checkMentalHealthActivityValue: Boolean = false,
    var checkNutritionActivityValue: Boolean = false
)
