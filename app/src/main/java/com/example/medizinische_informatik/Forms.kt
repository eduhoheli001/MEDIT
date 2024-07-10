package com.example.medizinische_informatik

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.mindrot.jbcrypt.BCrypt
import java.time.Instant
import java.time.ZoneId

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: ContactViewModel,
    mainActivity: MainActivity,
    promptManager: BiometricPromptManager
) {
    val state = viewModel.state
    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    val biometricResult by promptManager.promptResults.collectAsState(
        initial = null
    )
    val enrollLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            println("Activity result: $it")
        }
    )

    val biometricVerificationRequiredText = stringResource(id = R.string.biometric_verification_required)
    val biometricVerificationDescriptionText = stringResource(id = R.string.biometric_verification_description)
    val authenticationFailedText = stringResource(id = R.string.authentication_failed)
    val pleaseEnterValidEmailPasswordText = stringResource(id = R.string.please_enter_valid_email_password)

    LaunchedEffect(biometricResult) {
        if (biometricResult is BiometricPromptManager.BiometricResult.AuthenticationNotSet) {
            if (Build.VERSION.SDK_INT >= 30) {
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                    )
                }
                enrollLauncher.launch(enrollIntent)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.login),
            contentDescription = stringResource(id = R.string.login_bild),
            modifier = Modifier.size(200.dp))

        Text(text = stringResource(id = R.string.welcome_back), fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = stringResource(id = R.string.login_to_your_account))
        Spacer(modifier = Modifier.height(16.dp))

        ValidationTextField(
            mode = TextFieldMode.MAIL,
            value = email,
            label = stringResource(id = R.string.email_adresse),
            onChange = { email = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        PasswordTextField(
            text = password,
            onTextChanged = { password = it },
            labelText = stringResource(id = R.string.password)
        )
        Text(text = stringResource(id = R.string.new_account), modifier = Modifier.clickable {
            navController.navigate("register")
        })
        Spacer(modifier = Modifier.height(16.dp))
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
                    println("Authentication success")
                    Toast.makeText(mainActivity, stringResource(id = R.string.authentication_success), Toast.LENGTH_SHORT).show()
                    println("Login mit email: " + state.value.email)
                    navController.navigate("dashboard")
                }
            }
        }

        Button(onClick = {
            if (password.isNotEmpty() && email.isNotEmpty()) {
                viewModel.getAuthLog(email.lowercase()).observe(mainActivity, Observer { authList ->
                    if (authList.isNotEmpty() && BCrypt.checkpw(password, authList.first().password)) {
                        state.value = state.value.copy(
                            title = authList.first().title,
                            nachname = authList.first().nachname,
                            vorname = authList.first().vorname,
                            geburtsdatum = authList.first().geburtsdatum,
                            email = authList.first().email,
                            id = authList.first().id
                        )

                        promptManager.showBiometricPrompt(
                            title = biometricVerificationRequiredText ,
                            description = biometricVerificationDescriptionText
                        )
                    } else
                        Toast.makeText(mainActivity, authenticationFailedText, Toast.LENGTH_SHORT).show()
                })
            } else
                Toast.makeText(mainActivity, pleaseEnterValidEmailPasswordText , Toast.LENGTH_SHORT).show()
        }) {
            Text(text = stringResource(id = R.string.login))
        }
        //Spacer(modifier = Modifier.height(16.dp))

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserForm(
    viewModel: ContactViewModel,
    mainActivity: MainActivity
) {
    val stateAuth = viewModel.state

    // Strings aus strings.xml lesen
    val icd10Diagnosen = stringResource(id = R.string.icd10_diagnosen)
    val allergien = stringResource(id = R.string.allergien)
    val speichern = stringResource(id = R.string.speichern)

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.login),
            contentDescription = stringResource(id = R.string.login_bild),
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally)
        )
        BasicText(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.arztbezogene_daten),
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        var textFieldValue by remember { mutableStateOf("") }
        var listItems = remember { mutableStateListOf<String>() }
        var textFieldValueAllergy by remember { mutableStateOf("") }
        var listItemsAllergy = remember { mutableStateListOf<String>() }

        LaunchedEffect(mainActivity) {
            viewModel.getAllICD10().observe(mainActivity, Observer { diagList ->
                listItems.clear()
                diagList?.forEach {
                    listItems.add(it.name)
                }
            })

            viewModel.getAllAllergies().observe(mainActivity, Observer { allergiesList ->
                listItemsAllergy.clear()
                allergiesList?.forEach {
                    listItemsAllergy.add(it.name)
                }
            })
        }

        TextFieldAddButton(
            modifier = Modifier,
            placeholder = icd10Diagnosen,
            textFieldValue = textFieldValue,
            listItems = listItems,
            onChange = { newValue ->
                textFieldValue = newValue
            }
        )
        TextFieldAddButton(
            modifier = Modifier,
            placeholder = allergien,
            textFieldValue = textFieldValueAllergy,
            listItems = listItemsAllergy,
            onChange = { newValue ->
                textFieldValueAllergy = newValue
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            viewModel.saveData(stateAuth.value.id, listItems.toList(), listItemsAllergy.toList())
            // Zurück zum Dashboard navigieren
            viewModel.menuSelectedItemIndex.value = 0
        }) {
            Text(text = speichern)
        }
    }
}

@Composable
fun HealthForm(viewModel: ContactViewModel) {
    val healthFormState = viewModel.healthFormState
    val stateAuth = viewModel.state

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.login),
            contentDescription = "Login image",
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally)
        )

        BasicText(
            text = "Medizinische Daten",
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LabelledCheckBox(
            checked = healthFormState.value.checkMentalHealthActivityValue,
            onCheckedChange = { value ->
                healthFormState.value = healthFormState.value.copy(checkMentalHealthActivityValue = value)
            },
            label = "Wurden Mentaledaten aufgenommen?",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        if (healthFormState.value.checkMentalHealthActivityValue) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Wohlbefinden: ")
                    Slider(
                        value = healthFormState.value.dailyWellBeingValue.toFloat(),
                        onValueChange = { value ->
                            healthFormState.value = healthFormState.value.copy(dailyWellBeingValue = value.toInt())
                        },
                        valueRange = 1f..5f,
                        steps = 4,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    )
                    Text("${healthFormState.value.dailyWellBeingValue}/5")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Schmerzlevel: ")
                    Slider(
                        value = healthFormState.value.painLevelValue.toFloat(),
                        onValueChange = { value ->
                            healthFormState.value = healthFormState.value.copy(painLevelValue = value.toInt())
                        },
                        valueRange = 0f..10f,
                        steps = 10,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    )
                    Text("${healthFormState.value.painLevelValue}/10")
                }
            }
        }

        LabelledCheckBox(
            checked = healthFormState.value.checkNutritionActivityValue,
            onCheckedChange = { value ->
                healthFormState.value = healthFormState.value.copy(checkNutritionActivityValue = value)
            },
            label = "Wurden Ernährungsdaten aufgenommen?",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        if (healthFormState.value.checkNutritionActivityValue) {
            listOf(
                "Zuckerhaltige Lebensmittel gegessen" to healthFormState.value.sugarConsumptionValue,
                "Alkohol getrunken" to healthFormState.value.alcoholConsumptionValue,
                "Fleisch gegessen" to healthFormState.value.meatConsumptionValue,
                "Ei gegessen" to healthFormState.value.eggConsumptionValue,
                "Milch getrunken" to healthFormState.value.milkConsumptionValue,
                "Koffein getrunken" to healthFormState.value.caffeineConsumptionValue
            ).forEach { (label, selectedOption) ->
                TripleRadioButton(
                    selectedOption = selectedOption,
                    onOptionSelected = { value ->
                        when (label) {
                            "Zuckerhaltige Lebensmittel gegessen" -> healthFormState.value = healthFormState.value.copy(sugarConsumptionValue = value)
                            "Alkohol getrunken" -> healthFormState.value = healthFormState.value.copy(alcoholConsumptionValue = value)
                            "Fleisch gegessen" -> healthFormState.value = healthFormState.value.copy(meatConsumptionValue = value)
                            "Ei gegessen" -> healthFormState.value = healthFormState.value.copy(eggConsumptionValue = value)
                            "Milch getrunken" -> healthFormState.value = healthFormState.value.copy(milkConsumptionValue = value)
                            "Koffein getrunken" -> healthFormState.value = healthFormState.value.copy(caffeineConsumptionValue = value)
                        }
                    },
                    label = label,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        }

        LabelledCheckBox(
            checked = healthFormState.value.checkSportActivityValue,
            onCheckedChange = { value ->
                healthFormState.value = healthFormState.value.copy(checkSportActivityValue = value)
            },
            label = "Wurden Sportdaten aufgenommen?",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        if (healthFormState.value.checkSportActivityValue) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                ValidationTextField(
                    value = healthFormState.value.sportTypeValue,
                    onChange = { value ->
                        healthFormState.value = healthFormState.value.copy(sportTypeValue = value)
                    },
                    label = "Sportart"
                )
                Spacer(modifier = Modifier.height(8.dp))
                ValidationTextField(
                    value = healthFormState.value.sportDurationValue,
                    onChange = { value ->
                        healthFormState.value = healthFormState.value.copy(sportDurationValue = value)
                    },
                    label = "Dauer"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Intensität")
                    Slider(
                        value = healthFormState.value.sportIntensityValue.toFloat(),
                        onValueChange = { value ->
                            healthFormState.value = healthFormState.value.copy(sportIntensityValue = value.toInt())
                        },
                        valueRange = 6f..20f,
                        steps = 14,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    )
                    Text("${healthFormState.value.sportIntensityValue}/20")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                runBlocking {
                    viewModel.menuSelectedItemIndex.value = 0
                    if (healthFormState.value.checkSportActivityValue) {
                        viewModel.insertSportData(
                            sportTypeValue = healthFormState.value.sportTypeValue,
                            sportDurationValue = healthFormState.value.sportDurationValue,
                            sportIntensityValue = healthFormState.value.sportIntensityValue,
                            authentication_id = stateAuth.value.id
                        )
                    }
                    if (healthFormState.value.checkMentalHealthActivityValue) {
                        viewModel.insertMentalData(
                            dailyWellBeingValue = healthFormState.value.dailyWellBeingValue,
                            painLevelValue = healthFormState.value.painLevelValue,
                            authentication_id = stateAuth.value.id
                        )
                    }
                    if (healthFormState.value.checkNutritionActivityValue) {
                        viewModel.insertNutritionData(
                            sugarConsumptionValue = healthFormState.value.sugarConsumptionValue,
                            alcoholConsumptionValue = healthFormState.value.alcoholConsumptionValue,
                            meatConsumptionValue = healthFormState.value.meatConsumptionValue,
                            eggConsumptionValue = healthFormState.value.eggConsumptionValue,
                            milkConsumptionValue = healthFormState.value.milkConsumptionValue,
                            caffeineConsumptionValue = healthFormState.value.caffeineConsumptionValue,
                            authentication_id = stateAuth.value.id
                        )
                    }
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Speichern")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: ContactViewModel,
    mainActivity: MainActivity
) {
    val state = viewModel.regState
    var isPasswordStrong by rememberSaveable { mutableStateOf(false) }
    var hasPasswordError by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.login),
            contentDescription = stringResource(id = R.string.register_image_desc),
            modifier = Modifier.size(200.dp)
        )

        Text(text = stringResource(id = R.string.create_account), fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = stringResource(id = R.string.sign_up_to_get_started))
        Spacer(modifier = Modifier.height(16.dp))

        ValidationTextField(
            mode = TextFieldMode.NONE,
            value = state.value.title,
            label = stringResource(id = R.string.title),
            onChange = { value ->
                state.value = state.value.copy(title = value)
            }
        )

        ValidationTextField(
            mode = TextFieldMode.GENERIC,
            value = state.value.vorname,
            label = stringResource(id = R.string.first_name),
            onChange = { value ->
                state.value = state.value.copy(vorname = value)
            }
        )

        ValidationTextField(
            mode = TextFieldMode.GENERIC,
            value = state.value.nachname,
            label = stringResource(id = R.string.last_name),
            onChange = { value ->
                state.value = state.value.copy(nachname = value)
            }
        )

        ValidationTextField(
            mode = TextFieldMode.MAIL,
            value = state.value.email,
            label = stringResource(id = R.string.email_address),
            onChange = { newMail ->
                state.value = state.value.copy(email = newMail)
            }
        )

        PasswordTextField(
            text = state.value.password,
            onTextChanged = { newPassword ->
                state.value = state.value.copy(password = newPassword)
                hasPasswordError = newPassword.isEmpty() || !isPasswordStrong
            },
            labelText = stringResource(id = R.string.password),
            validateStrengthPassword = true,
            hasError = hasPasswordError,
            onHasStrongPassword = { isStrong ->
                isPasswordStrong = isStrong
            }
        )

        ConfirmPasswordTextField(
            text = state.value.password2,
            confirmText = state.value.password,
            labelText = stringResource(id = R.string.confirm_password),
            onTextChanged = { confirmPassword ->
                state.value = state.value.copy(password2 = confirmPassword)
            }
        )

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )

        val datepickerDateString = datePickerState.selectedDateMillis?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
        }.toString()

        DatePicker(
            state = datePickerState,
            modifier = Modifier.padding(horizontal = 24.dp),
            title = null,
            headline = {
                Text(
                    text = stringResource(id = R.string.birthdate),
                    style = TextStyle(fontSize = 18.sp),
                )
            },
        )

        val coroutineScope = rememberCoroutineScope()

        Button(onClick = {
            println("datepickerDateString: $datepickerDateString")

            if (state.value.password == state.value.password2 &&
                state.value.vorname.isNotEmpty() &&
                state.value.nachname.isNotEmpty() &&
                state.value.email.isNotEmpty() &&
                state.value.password.isNotEmpty() &&
                isPasswordStrong
            ) {
                coroutineScope.launch {
                    println(BCrypt.hashpw(state.value.password, BCrypt.gensalt()))

                    val success = viewModel.insertAuth(
                        title = state.value.title,
                        vorname = state.value.vorname,
                        nachname = state.value.nachname,
                        geburtsdatum = datepickerDateString,
                        email = state.value.email.lowercase(),
                        password = BCrypt.hashpw(state.value.password, BCrypt.gensalt())
                    )

                    if (success) {
                        navController.navigate("login")
                        println("Should navigate to login")
                    } else {
                        println("Error adding authentication")
                    }
                }
            } else {
                var errorhandler = ""
                println("else errorhandler")
                if (state.value.password != state.value.password2)
                    errorhandler += mainActivity.getString(R.string.password_mismatch_error)
                if (state.value.vorname.isEmpty())
                    errorhandler += mainActivity.getString(R.string.empty_first_name_error)
                if (state.value.nachname.isEmpty())
                    errorhandler += mainActivity.getString(R.string.empty_last_name_error)
                if (state.value.email.isEmpty())
                    errorhandler += mainActivity.getString(R.string.empty_email_error)
                if (state.value.password.isEmpty() || isPasswordStrong)
                    errorhandler += mainActivity.getString(R.string.empty_password_error)
                if (datepickerDateString.isEmpty())
                    errorhandler += mainActivity.getString(R.string.empty_birthdate_error)

                Toast.makeText(mainActivity, mainActivity.getString(R.string.registration_failed) + errorhandler, Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = stringResource(id = R.string.register))
        }
        Spacer(modifier = Modifier.height(32.dp))

        Text(text = stringResource(id = R.string.already_have_account), modifier = Modifier.clickable {
            navController.navigate("login")
        })
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun DoctorForm(
    viewModel: ContactViewModel,
    mainActivity: MainActivity
) {
    val state = viewModel.physState
    val stateAuth = viewModel.state

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.login),
            contentDescription = stringResource(id = R.string.login_image_desc),
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally)
        )
        BasicText(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.doctor_form_title),
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        ValidationTextField(
            mode = TextFieldMode.NONE,
            value = state.value.title,
            label = stringResource(id = R.string.title),
            onChange = { value ->
                state.value = state.value.copy(title = value)
            }
        )
        ValidationTextField(
            mode = TextFieldMode.GENERIC,
            value = state.value.vorname,
            label = stringResource(id = R.string.first_name),
            onChange = { value ->
                state.value = state.value.copy(vorname = value)
            }
        )
        ValidationTextField(
            mode = TextFieldMode.GENERIC,
            value = state.value.nachname,
            label = stringResource(id = R.string.last_name),
            onChange = { value ->
                state.value = state.value.copy(nachname = value)
            }
        )
        ValidationTextField(
            mode = TextFieldMode.MAIL,
            value = state.value.email,
            label = stringResource(id = R.string.email_address),
            onChange = { value ->
                state.value = state.value.copy(email = value)
            }
        )
        ValidationTextField(
            mode = TextFieldMode.GENERIC,
            value = state.value.fachgebiet,
            label = stringResource(id = R.string.specialty),
            onChange = { value ->
                state.value = state.value.copy(fachgebiet = value)
            }
        )

        val coroutineScope = rememberCoroutineScope()
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            if (state.value.vorname.isNotEmpty() &&
                state.value.nachname.isNotEmpty() &&
                state.value.email.isNotEmpty() &&
                state.value.fachgebiet.isNotEmpty()
            ) {
                coroutineScope.launch {
                    print("stateAuth.value.title${stateAuth.value.id}")
                    print("state.value.vorname${state.value.vorname}")
                    print("state.value.nachname${state.value.nachname}")
                    print("state.value.email${state.value.email}")

                    val success = viewModel.insertPhys(
                        title = state.value.title,
                        vorname = state.value.vorname,
                        nachname = state.value.nachname,
                        fachgebiet = state.value.fachgebiet,
                        email = state.value.email.lowercase(),
                        authentication_id = stateAuth.value.id
                    )

                    if (success) {
                        println("Should navigate to dashboard")
                        viewModel.menuSelectedItemIndex.value = 0
                    } else {
                        println("Error adding phys.")
                    }
                }
            } else {
                var errorhandler = ""
                println("else errorhandler")

                if (state.value.vorname.isEmpty())
                    errorhandler += mainActivity.getString(R.string.empty_first_name_error)
                if (state.value.nachname.isEmpty())
                    errorhandler += mainActivity.getString(R.string.empty_last_name_error)
                if (state.value.email.isEmpty())
                    errorhandler += mainActivity.getString(R.string.empty_email_error)
                if (state.value.fachgebiet.isEmpty())
                    errorhandler += mainActivity.getString(R.string.empty_specialty_error)

                Toast.makeText(mainActivity, mainActivity.getString(R.string.save_failed) + errorhandler, Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = stringResource(id = R.string.save))
        }
    }
}


//Helper templates
@Composable
fun HealthDataDisplay(value: Int):String {
    //falls notwendig könnte es in Datenbank ausgelagert werden
    val displayText = when (value) {
        0 -> stringResource(id = R.string.radio_button_option_yes)
        1 -> stringResource(id = R.string.radio_button_option_no)
        2 -> stringResource(id = R.string.radio_button_option_maybe)
        else -> stringResource(id = R.string.text_field_add_unknown)
    }

    return displayText
}
@Composable
fun PainLevelDisplay(painLevelValue: Int): String {
    return when (painLevelValue) {
        0 -> stringResource(id = R.string.pain_level_0)
        1 -> stringResource(id = R.string.pain_level_1)
        2 -> stringResource(id = R.string.pain_level_2)
        3 -> stringResource(id = R.string.pain_level_3)
        4 -> stringResource(id = R.string.pain_level_4)
        5 -> stringResource(id = R.string.pain_level_5)
        6 -> stringResource(id = R.string.pain_level_6)
        7 -> stringResource(id = R.string.pain_level_7)
        8 -> stringResource(id = R.string.pain_level_8)
        9 -> stringResource(id = R.string.pain_level_9)
        10 -> stringResource(id = R.string.pain_level_10)
        else -> stringResource(id = R.string.pain_level_unknown)
    }
}

@Composable
fun WellbeingDisplay(wellbeingValue: Int): String {
    return when (wellbeingValue) {
        1 -> stringResource(id = R.string.wellbeing_1)
        2 -> stringResource(id = R.string.wellbeing_2)
        3 -> stringResource(id = R.string.wellbeing_3)
        4 -> stringResource(id = R.string.wellbeing_4)
        5 -> stringResource(id = R.string.wellbeing_5)
        else -> stringResource(id = R.string.wellbeing_unknown)
    }
}