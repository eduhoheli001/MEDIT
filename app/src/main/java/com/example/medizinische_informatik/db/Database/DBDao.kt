package com.example.medizinische_informatik.db.Database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AuthDBDao {

    @Query("Select * from authentication")
    fun getAllAuth(): LiveData<List<Authentication>>

    @Insert
    fun addAuth(authentication: Authentication)

    @Insert
    fun addPhysician(physician: Physician)

    @Insert
    fun addDiagnosis(icD10: ICD10)

    @Insert
    fun addAllergie(allergies: Allergies)

    @Insert
    fun addMentalData(mentalData: MentalData)

    @Insert
    fun addNutritionData(nutritionData: NutritionData)

    @Insert
    fun addSportData(sportData: SportData)


    @Query("Delete from allergies where authentication_id=:authentication_id")
    fun deleteAllAllergies(authentication_id: Int)

    @Query("Delete from icd10 where authentication_id=:authentication_id")
    fun deleteAllICD10(authentication_id: Int)






    /*@Query("SELECT COUNT(*) FROM authentication WHERE email = :email")
    suspend fun getAuthCount(email: String): Int
    @Query("SELECT * from authentication WHERE id = :id")
    fun getAuth(id: Int): Flow<Authentication>
*/
    //all
    @Query("SELECT * from authentication WHERE email = :email")
    fun getAuthLogin(email: String): LiveData<List<Authentication>>

    @Query("SELECT * FROM icd10 WHERE authentication_id = :authentication_id")
    fun getAllDiagnosis(authentication_id: Int): LiveData<List<ICD10>>

    @Query("SELECT * FROM allergies WHERE authentication_id = :authentication_id")
    fun getAllAllergies(authentication_id: Int): LiveData<List<Allergies>>

    @Query("SELECT * FROM physician WHERE authentication_id = :authentication_id")
    fun getAllPhysician(authentication_id: Int): LiveData<List<Physician>>

    @Query("SELECT * FROM authentication WHERE id = :id")
    fun getAllAuthentication(id: Int): LiveData<List<Authentication>>

    @Query("SELECT * FROM mental_data WHERE authentication_id = :authentication_id")
    fun getAllMentalData(authentication_id: Int): LiveData<List<MentalData>>
    @Query("SELECT * FROM nutrition_data WHERE authentication_id = :authentication_id")
    fun getAllNutritionData(authentication_id: Int): LiveData<List<NutritionData>>
    @Query("SELECT * FROM sport_data WHERE authentication_id = :authentication_id")
    fun getAllSportData(authentication_id: Int): LiveData<List<SportData>>

}

