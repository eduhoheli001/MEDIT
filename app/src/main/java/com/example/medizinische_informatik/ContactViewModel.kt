package com.example.medizinische_informatik


import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medizinische_informatik.db.Database.Allergies
import com.example.medizinische_informatik.db.Database.Authentication
import com.example.medizinische_informatik.db.Database.FormPhysState
//import com.example.medizinische_informatik.db.DBDao
import com.example.medizinische_informatik.db.Database.FormStates
import com.example.medizinische_informatik.db.Database.HealthFormState
import com.example.medizinische_informatik.db.Database.ICD10
import com.example.medizinische_informatik.db.Database.MentalData
import com.example.medizinische_informatik.db.Database.NutritionData
import com.example.medizinische_informatik.db.Database.Physician
import com.example.medizinische_informatik.db.Database.RegisterStates
import com.example.medizinische_informatik.db.Database.SportData
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CompletableDeferred

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


class ContactViewModel: ViewModel() {
    //var menuSelectedItemIndex = mutableStateOf(0)
    val menuSelectedItemIndex = mutableStateOf(0)
    val mainDao = HealthApplication.healthDatabase.getAuthDao()

    var state = mutableStateOf(FormStates())
    var regState = mutableStateOf(RegisterStates())
    var physState = mutableStateOf(FormPhysState())
    var healthFormState = mutableStateOf(HealthFormState())


    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if(!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }



//Dp requests
    fun getAllICD10(): LiveData<List<ICD10>> {
        return mainDao.getAllDiagnosis(state.value.id)
    }
    fun getAllAllergies(): LiveData<List<Allergies>> {
        return mainDao.getAllAllergies(state.value.id)
    }
    fun getAllPhysicians(): LiveData<List<Physician>> {
        return mainDao.getAllPhysician(state.value.id)
    }

    fun getAllMentalData(): LiveData<List<MentalData>> {
        return mainDao.getAllMentalData(state.value.id)
    }
    fun getAllNutritionData(): LiveData<List<NutritionData>> {
        return mainDao.getAllNutritionData(state.value.id)
    }
    fun getAllSportData(): LiveData<List<SportData>> {
        return mainDao.getAllSportData(state.value.id)
    }

    fun getAllAuthentication(): LiveData<List<Authentication>> {
        return mainDao.getAllAuthentication(state.value.id)
    }


    //insert into DB
    suspend fun insertAuth(
        title: String,
        vorname:String,
        nachname:String,
        email: String,
        password: String,
        geburtsdatum: String
    ): Boolean {
        val deferred = CompletableDeferred<Boolean>()

        viewModelScope.launch(Dispatchers.IO) {
            val auth = Authentication(
                title= title,
                vorname=vorname,
                nachname=nachname,
                email=email.lowercase(),//-> email is always lowercase
                password = password,
                geburtsdatum=geburtsdatum)

            try {
                mainDao.addAuth(auth)
                deferred.complete(true)
            } catch (e: Exception) {
                println("ERROR:$e")
                deferred.complete(false)
            }
        }

        return deferred.await()
    }

    suspend fun insertPhys(
        title: String,
        vorname:String,
        nachname:String,
        email: String,
        fachgebiet: String,
        authentication_id: Int
    ): Boolean {
        val deferred = CompletableDeferred<Boolean>()

        viewModelScope.launch(Dispatchers.IO) {
            val auth = Physician(
                title= title,
                vorname=vorname,
                nachname=nachname,
                email=email.lowercase(),//-> email is always lowercase
                fachgebiet = fachgebiet,
                authentication_id = authentication_id)

            try {
                mainDao.addPhysician(auth)
                deferred.complete(true)
            } catch (e: Exception) {
                println("ERROR:$e")
                deferred.complete(false)
            }
        }

        return deferred.await()
    }


    //remove
    fun deleteAllICD10(authentication_id: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            mainDao.deleteAllICD10(authentication_id)
        }
    }
    fun deleteAllAllergies(authentication_id: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            mainDao.deleteAllAllergies(authentication_id)
        }
    }

    fun insertDiagnosis(name: String, authentication_id: Int) = viewModelScope.launch {
        try {
            withContext(Dispatchers.IO) {
                mainDao.addDiagnosis(ICD10(name = name, authentication_id = authentication_id))
            }
        } catch (e: Exception) {
            println("MainViewModel Error inserting ICD10: ${e.message}")
        }
    }

    fun insertAllergies(name: String, authentication_id: Int) = viewModelScope.launch {
        try {
            withContext(Dispatchers.IO) {
                mainDao.addAllergie(Allergies(name = name, authentication_id = authentication_id))
            }
        } catch (e: Exception) {
            println("MainViewModel Error inserting Allergies: ${e.message}")
        }
    }
    fun saveData(stateAuthId: Int, icd10List: List<String>, allergiesList: List<String>) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            mainDao.deleteAllICD10(stateAuthId)
            mainDao.deleteAllAllergies(stateAuthId)
        }

        icd10List.forEach { name ->
            insertDiagnosis(name, stateAuthId)
        }

        allergiesList.forEach { name ->
            insertAllergies(name, stateAuthId)
        }
    }

    fun insertMentalData(dailyWellBeingValue: Int, painLevelValue: Int, authentication_id: Int) = viewModelScope.launch {
        try {
            withContext(Dispatchers.IO) {
                mainDao.addMentalData(MentalData(dailyWellBeingValue = dailyWellBeingValue, painLevelValue = painLevelValue, authentication_id = authentication_id))
            }
        } catch (e: Exception) {
            println("MainViewModel Error inserting Mental Data: ${e.message}")
        }
    }

    fun insertNutritionData(
        sugarConsumptionValue: Int,
        alcoholConsumptionValue: Int,
        meatConsumptionValue: Int,
        eggConsumptionValue: Int,
        milkConsumptionValue: Int,
        caffeineConsumptionValue: Int,
        authentication_id: Int
    ) = viewModelScope.launch {
        try {
            withContext(Dispatchers.IO) {
                mainDao.addNutritionData(
                    NutritionData(
                        sugarConsumptionValue = sugarConsumptionValue,
                        alcoholConsumptionValue = alcoholConsumptionValue,
                        meatConsumptionValue = meatConsumptionValue,
                        eggConsumptionValue = eggConsumptionValue,
                        milkConsumptionValue = milkConsumptionValue,
                        caffeineConsumptionValue = caffeineConsumptionValue,
                        authentication_id = authentication_id
                    )
                )
            }
        } catch (e: Exception) {
            println("MainViewModel Error inserting Nutrition Data: ${e.message}")
        }
    }
    fun insertSportData(
        sportTypeValue: String,
        sportDurationValue: String,
        sportIntensityValue: Int,
        authentication_id: Int
    ) = viewModelScope.launch {
        try {
            withContext(Dispatchers.IO) {
                mainDao.addSportData(
                    SportData(
                        sportTypeValue = sportTypeValue,
                        sportDurationValue = sportDurationValue,
                        sportIntensityValue = sportIntensityValue,
                        authentication_id = authentication_id
                    )
                )
            }
        } catch (e: Exception) {
            println("MainViewModel Error inserting Sport Data: ${e.message}")
        }
    }


    fun getAuthLog(email:String): LiveData<List<Authentication>> {
        if (email.isBlank()) {
            throw IllegalArgumentException("Email cannot be blank")
        }
        return mainDao.getAuthLogin(email)
    }

}
