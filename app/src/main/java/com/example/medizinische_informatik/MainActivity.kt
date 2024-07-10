package com.example.medizinische_informatik
import android.provider.Settings

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medizinische_informatik.AESUtils.AESUtils
import com.example.medizinische_informatik.PDF.generatePdf
import com.example.medizinische_informatik.navigation.ScaffoldComposable
import com.example.medizinische_informatik.permissions.PermissionDialog
import com.example.medizinische_informatik.permissions.ReadExternalStoragePermissionTextProvider
import com.example.medizinische_informatik.permissions.WriteExternalStoragePermissionTextProvider
import com.example.medizinische_informatik.ui.theme.Medizinische_InformatikTheme
import com.google.gson.GsonBuilder


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: ContactViewModel
    private val promptManager by lazy {
        BiometricPromptManager(this)
    }
    private val permissionsToRequest: Array<String>
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        }

    @SuppressLint("StateFlowValueCalledInComposition")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[ContactViewModel::class.java]

        setContent {
            Medizinische_InformatikTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "login"//login
                )   {
                    composable("dashboard") {
                       ScaffoldComposable(viewModel = viewModel,navController,this@MainActivity,permissionsToRequest,promptManager)
                    }
                    composable("login") {
                       LoginScreen(navController, viewModel = viewModel,this@MainActivity,promptManager)
                    }
                    composable("register") {
                        RegisterScreen(navController = navController, viewModel = viewModel,this@MainActivity)
                    }
                }
            }
        }
    }
    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permission Denied..", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }*/
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: ContactViewModel,
    mainActivity: MainActivity,
    permissionsToRequest: Array<String>,
    promptManager: BiometricPromptManager
) {


    val dialogQueue = viewModel.visiblePermissionDialogQueue

    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            permissionsToRequest.forEach { permission ->
                viewModel.onPermissionResult(
                    permission = permission,
                    isGranted = perms[permission] == true
                )
            }
        }
    )


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BasicText(
                text = "Digitales Gesundheitstagebuch",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(vertical = 16.dp)
            )
            BasicText(
                text = "Kurzbeschreibung:",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp
                )
            )
            BasicText(
                text = "Diese App ermöglicht die Verwaltung und Ansicht von medizinischen Daten sowie persönlichen Informationen in einem digitalen Gesundheitstagebuch. Die Funktionen umfassen die Erfassung von Kursterminen, mentalem Wohlbefinden, Ernährungsgewohnheiten und sportlichen Aktivitäten.",
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
            )
            BasicText(
                text = "Zielgruppe:",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp
                )
            )
            BasicText(
                text = "Die Zielgruppe umfasst Personen im Alter von 18-40 Jahren mit Interesse an Gesundheit, Fitness und einem gesunden Lebensstil sowie fortgeschrittenen technologischen Kenntnissen.",
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
            )
            BasicText(
                text = "Gespeicherte Daten:",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp
                )
            )
            var showPhysician by remember { mutableStateOf(false) }
            LabelledCheckBox(
                checked = showPhysician,
                onCheckedChange = { showPhysician = it },
                label = "Ärzte anzeigen",
                modifier = Modifier.padding(bottom = 2.dp)
            )

            if (showPhysician)
                PhysicianList(viewModel)

            var showDiag by remember { mutableStateOf(false) }
            LabelledCheckBox(
                checked = showDiag,
                onCheckedChange = { showDiag = it },
                label = "Diagnosen anzeigen",
                modifier = Modifier.padding(bottom = 2.dp)
            )

            if (showDiag)
                showMedData(1,viewModel)

            var showAllergies by remember { mutableStateOf(false) }
            LabelledCheckBox(
                checked = showAllergies,
                onCheckedChange = { showAllergies = it },
                label = "Allergien anzeigen",
                modifier = Modifier.padding(bottom = 2.dp)
            )

            if (showAllergies)
                showMedData(2,viewModel)

            var showMental by remember { mutableStateOf(false) }
            LabelledCheckBox(
                checked = showMental,
                onCheckedChange = { showMental = it },
                label = "Mentalegesundheitsdaten anzeigen",
                modifier = Modifier.padding(bottom = 2.dp)
            )

            if (showMental)
                showSkala(1,viewModel)


            var showNutrition by remember { mutableStateOf(false) }
            LabelledCheckBox(
                checked = showNutrition,
                onCheckedChange = { showNutrition = it },
                label = "Ernährungsdaten anzeigen",
                modifier = Modifier.padding(bottom = 2.dp)
            )

            if (showNutrition)
                showSkala(2,viewModel)


            var showSport by remember { mutableStateOf(false) }
            LabelledCheckBox(
                checked = showSport,
                onCheckedChange = { showSport = it },
                label = "Sportdaten anzeigen",
                modifier = Modifier.padding(bottom = 2.dp)
            )

            if (showSport)
                showSkala(3,viewModel)

            createJson(viewModel,mainActivity)

            pdfGenerator(viewModel,promptManager,mainActivity);
            dialogQueue
                .reversed()
                .forEach { permission ->
                    PermissionDialog(
                        permissionTextProvider = when (permission) {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                                WriteExternalStoragePermissionTextProvider()
                            }
                           Manifest.permission.READ_EXTERNAL_STORAGE -> {
                               ReadExternalStoragePermissionTextProvider()
                            }
                           //hier könnten mehr hinzugefügt werden
                            else -> return@forEach
                        },
                        isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                            mainActivity, permission
                        ),
                        onDismiss = viewModel::dismissDialog,
                        onOkClick = {
                            viewModel.dismissDialog()
                           multiplePermissionResultLauncher.launch(
                                arrayOf(permission)
                            )
                        },
                        onGoToAppSettingsClick = mainActivity::openAppSettings
                    )
                }
        }
    }
}
fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}
private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun createJson(viewModel: ContactViewModel, context: Context) {
    val physicianList by viewModel.getAllPhysicians().observeAsState(emptyList())
    val mentalDataList by viewModel.getAllMentalData().observeAsState(emptyList())
    val nutritionDataList by viewModel.getAllNutritionData().observeAsState(emptyList())
    val sportDataList by viewModel.getAllSportData().observeAsState(emptyList())
    val allergiesList by viewModel.getAllAllergies().observeAsState(emptyList())
    val icd10List by viewModel.getAllICD10().observeAsState(emptyList())

    // Erstellen der Datenstruktur für JSON
    val dataMap = mapOf(
        "physicians" to physicianList,
        "mentalData" to mentalDataList,
        "nutritionData" to nutritionDataList,
        "sportData" to sportDataList,
        "allergies" to allergiesList,
        "icd10" to icd10List
    )

    // Konvertieren der Daten in JSON
    val gson = GsonBuilder().setPrettyPrinting().create()
    val jsonData = gson.toJson(dataMap)

    // Random Key
    val keyString = "yNwAq62xAQb1pRMPf3G1vV4e8lW5v/Y8JfRJyN5SX6s="
    val secretKey = AESUtils.keyFromString(keyString)
    var decryptedData by remember { mutableStateOf("") }

    Button(onClick = {
        val (encryptedData, iv) = AESUtils.encrypt(jsonData, secretKey)
        val secretKeyDecrypt = AESUtils.keyFromString(keyString)
        decryptedData = AESUtils.decrypt(encryptedData, secretKeyDecrypt, iv)

        println("###decryptedData###")
        println(decryptedData)
        println("###encryptedData###")
        println(encryptedData)

        saveJson(context, encryptedData, "data.json")
    }) {
        Text(text = "Speichern")
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun saveJson(context: Context, jsonData: String, fileName: String): Uri? {
    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }
    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
    if (uri != null) {
        resolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(jsonData.toByteArray())
            outputStream.close()
            Toast.makeText(context, "JSON saved to Downloads: $fileName", Toast.LENGTH_SHORT).show()
            return uri
        }
    }
    Toast.makeText(context, "Failed to save JSON", Toast.LENGTH_SHORT).show()
    return null
}

@Composable
fun PhysicianList(viewModel: ContactViewModel) {
    val physicianList by viewModel.getAllPhysicians().observeAsState(emptyList())
    Column {
        if(physicianList.count() > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Name",
                    modifier = Modifier
                        .weight(1F)
                        .align(Alignment.CenterVertically)
                )

                Text(
                    text = "Email",
                    modifier = Modifier
                        .weight(1F)
                        .align(Alignment.CenterVertically)
                )
            }
        }
        physicianList.forEach { physician ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text =
                    physician.title+" "+physician.vorname+" "+physician.nachname,
                    modifier = Modifier
                        .weight(1F)
                        .align(Alignment.CenterVertically)
                )

                Text(
                    text = physician.email,
                    modifier = Modifier
                        .weight(1F)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
fun showSkala(
    mode: Int,
    viewModel: ContactViewModel
) {
    var saveNumber = 1
    val mentalDataList by viewModel.getAllMentalData().observeAsState(emptyList())
    val nutritionDataList by viewModel.getAllNutritionData().observeAsState(emptyList())
    val sportDataList by viewModel.getAllSportData().observeAsState(emptyList())

    Column {
        if(mode== 1 && mentalDataList.count() > 0 ||
           mode == 2 && nutritionDataList.count()>0 ||
           mode ==3 && sportDataList.count()>0)
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Nummer",
                    modifier = Modifier
                        .weight(1F)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = "Details",
                    modifier = Modifier
                        .weight(3F)
                        .align(Alignment.CenterVertically)
                )
            }
        }

        when (mode) {
            1 -> mentalDataList.forEach { data ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = saveNumber.toString(),
                        modifier = Modifier
                            .weight(1F)
                            .align(Alignment.CenterVertically)
                    )
                    saveNumber++
                    Text(
                        text = "Wohlbefinden: ${WellbeingDisplay(data.dailyWellBeingValue)}\nSchmerzlevel: ${PainLevelDisplay(data.painLevelValue)}",
                        modifier = Modifier
                            .weight(3F)
                            .align(Alignment.CenterVertically)
                    )
                }
            }
            2 -> nutritionDataList.forEach { data ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = saveNumber.toString(),
                        modifier = Modifier
                            .weight(1F)
                            .align(Alignment.CenterVertically)
                    )
                    saveNumber++
                    Text(
                        text =
                        "Zucker: ${HealthDataDisplay(data.sugarConsumptionValue)}\n" +
                        "Alkohol: ${HealthDataDisplay(data.alcoholConsumptionValue)}\n"+
                        "Fleisch: ${HealthDataDisplay(data.meatConsumptionValue)}\n"+
                        "Ei: ${HealthDataDisplay(data.eggConsumptionValue)}\n"+
                        "Milch: ${HealthDataDisplay(data.milkConsumptionValue)}\n"+
                        "Kaffee: ${HealthDataDisplay(data.caffeineConsumptionValue)}",
                        modifier = Modifier
                            .weight(3F)
                            .align(Alignment.CenterVertically)
                    )
                }
            }
            3 -> sportDataList.forEach { data ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = saveNumber.toString(),
                        modifier = Modifier
                            .weight(1F)
                            .align(Alignment.CenterVertically)
                    )
                    saveNumber++
                    Text(
                        text = "Type: ${data.sportTypeValue}\nDauer: ${data.sportDurationValue}\nIntensität: ${data.sportIntensityValue}",
                        modifier = Modifier
                            .weight(3F)
                            .align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

@Composable
fun showMedData(
    mode: Int,
    viewModel: ContactViewModel
) {
    var saveNumber=1;
    val physicianList by viewModel.getAllICD10().observeAsState(emptyList())
    val AllergiesList by viewModel.getAllAllergies().observeAsState(emptyList())
    Column {
        if((mode==1 && physicianList.count() > 0) || (mode !=1 && AllergiesList.count()>0)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Nummer",
                    modifier = Modifier
                        .weight(1F)
                        .align(Alignment.CenterVertically)
                )

                Text(
                    text = "Name",
                    modifier = Modifier
                        .weight(3F)
                        .align(Alignment.CenterVertically)
                )
            }
        }
        if(mode==1) {
            physicianList.forEach { diag ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = saveNumber.toString(),
                        modifier = Modifier
                            .weight(1F)
                            .align(Alignment.CenterVertically)
                    )
                    saveNumber++;
                    Text(
                        text = diag.name,
                        modifier = Modifier
                            .weight(3F)
                            .align(Alignment.CenterVertically)
                    )
                }
            }
        }
        else{
            AllergiesList.forEach { allergie ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = saveNumber.toString(),
                        modifier = Modifier
                            .weight(1F)
                            .align(Alignment.CenterVertically)
                    )
                    saveNumber++;
                    Text(
                        text = allergie.name,
                        modifier = Modifier
                            .weight(3F)
                            .align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}
@Composable
fun pdfGenerator(viewModel: ContactViewModel, promptManager: BiometricPromptManager, mainActivity: MainActivity) {
    val ctx = LocalContext.current

    val physicianList by viewModel.getAllPhysicians().observeAsState(emptyList())
    val mentalDataList by viewModel.getAllMentalData().observeAsState(emptyList())
    val nutritionDataList by viewModel.getAllNutritionData().observeAsState(emptyList())
    val sportDataList by viewModel.getAllSportData().observeAsState(emptyList())
    val medicalDataList by viewModel.getAllICD10().observeAsState(emptyList())
    val allergiesList by viewModel.getAllAllergies().observeAsState(emptyList())

    //PDF inhalt generieren
    val physicianDisplayList = physicianList.map { physician ->
        "${physician.title} ${physician.vorname} ${physician.nachname} - ${physician.email}"
    }
    val mentalDisplayList = mentalDataList.mapIndexed { index, data ->
        "Nummer: ${index + 1}\nWohlbefinden: ${WellbeingDisplay(data.dailyWellBeingValue)}\nSchmerzlevel: ${PainLevelDisplay(data.painLevelValue)}"
    }
    val nutritionDisplayList = nutritionDataList.mapIndexed { index, data ->
        "Nummer: ${index + 1}\nZucker: ${HealthDataDisplay(data.sugarConsumptionValue)}\nAlkohol: ${HealthDataDisplay(data.alcoholConsumptionValue)}\nFleisch: ${HealthDataDisplay(data.meatConsumptionValue)}\nEi: ${HealthDataDisplay(data.eggConsumptionValue)}\nMilch: ${HealthDataDisplay(data.milkConsumptionValue)}\nKaffee: ${HealthDataDisplay(data.caffeineConsumptionValue)}"
    }
    val sportDisplayList = sportDataList.mapIndexed { index, data ->
        "Nummer: ${index + 1}\nType: ${data.sportTypeValue}\nDauer: ${data.sportDurationValue}\nIntensität: ${data.sportIntensityValue}"
    }
    val medicalDisplayList = medicalDataList.mapIndexed { index, data ->
        "Nummer: ${index + 1}\nName: ${data.name}"
    }
    val allergiesDisplayList = allergiesList.mapIndexed { index, data ->
        "Nummer: ${index + 1}\nName: ${data.name}"
    }



    //BiometricVerification
    val biometricVerificationRequiredText = stringResource(id = R.string.biometric_verification_required)
    val biometricVerificationDescriptionText = stringResource(id = R.string.biometric_verification_description)
    val biometricResult by promptManager.promptResults.collectAsState(
        initial = null
    )
    val enrollLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            println("Activity result: $it")
        }
    )
    LaunchedEffect(biometricResult) {
        if (biometricResult is BiometricPromptManager.BiometricResult.AuthenticationNotSet) {
            if (Build.VERSION.SDK_INT >= 30) {
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                }
                enrollLauncher.launch(enrollIntent)
            }
        }
    }


        Button(
            modifier = Modifier,
            onClick = {
                promptManager.showBiometricPrompt(
                    title = biometricVerificationRequiredText ,
                    description = biometricVerificationDescriptionText
                )


            }) {
            Text("Signiertes PDF generieren")
        }
        biometricResult?.let { result ->
            when (result) {
                is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                    Toast.makeText(mainActivity, result.error, Toast.LENGTH_SHORT).show()
                }
                BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
                    Toast.makeText(mainActivity, stringResource(id = R.string.authentication_failed), Toast.LENGTH_SHORT).show()
                }
                BiometricPromptManager.BiometricResult.AuthenticationNotSet -> {
                    Toast.makeText(mainActivity, stringResource(id = R.string.authentication_not_set), Toast.LENGTH_SHORT).show()
                }
                BiometricPromptManager.BiometricResult.FeatureUnavailable -> {
                    Toast.makeText(mainActivity, stringResource(id = R.string.feature_unavailable), Toast.LENGTH_SHORT).show()
                }
                BiometricPromptManager.BiometricResult.HardwareUnavailable -> {
                    Toast.makeText(mainActivity, stringResource(id = R.string.hardware_unavailable), Toast.LENGTH_SHORT).show()
                }
                BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
                    generatePdf(
                        context = ctx,
                        physicianList = physicianDisplayList,
                        mentalDataList = mentalDisplayList,
                        nutritionDataList = nutritionDisplayList,
                        sportDataList = sportDisplayList,
                        medicalDataList = medicalDisplayList,
                        allergiesList = allergiesDisplayList
                    )
                    println("Authentication success")
                    Toast.makeText(mainActivity, stringResource(id = R.string.authentication_PDF_success), Toast.LENGTH_SHORT).show()
                }
            }
        }

}