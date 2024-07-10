package com.example.medizinische_informatik.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.medizinische_informatik.BiometricPromptManager
import com.example.medizinische_informatik.ContactViewModel
import com.example.medizinische_informatik.DashboardScreen
import com.example.medizinische_informatik.DoctorForm
import com.example.medizinische_informatik.HealthForm
import com.example.medizinische_informatik.MainActivity
import com.example.medizinische_informatik.R
import com.example.medizinische_informatik.UserForm
import com.example.medizinische_informatik.db.Database.MenuItem
import kotlinx.coroutines.launch


@Composable
fun AppBar(
    onNavigationIconClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.app_name_display))
        },
        backgroundColor = colorResource(id = R.color.skyblue3),
        contentColor = Color.Black,
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Toggle drawer"
                )
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScaffoldComposable(
    viewModel: ContactViewModel,
    navController: NavHostController,
    mainActivity: MainActivity,
    permissionsToRequest: Array<String>,
    promptManager: BiometricPromptManager
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier
            .background(Color.DarkGray)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    if (dragAmount > 0) {
                        scope.launch {
                            scaffoldState.drawerState.open()
                        }
                    }
                }
            },
        topBar = {
            AppBar(
                onNavigationIconClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                }
            )
        },
        drawerGesturesEnabled = true,
        drawerContent = {
            DrawerHeader()
            DrawerBody(
                items = listOf(
                    MenuItem(
                        id = 0,
                        tempId = "home",
                        title = "Home",
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home,
                    ),
                    MenuItem(
                        id = 1,
                        tempId = "patientd",
                        title = "Patientendaten",
                        selectedIcon = Icons.Filled.Info,
                        unselectedIcon = Icons.Outlined.Info,
                    ),
                    MenuItem(
                        id = 2,
                        tempId = "doctor",
                        title = "Ärzteinformationen",
                        selectedIcon = Icons.Filled.Info,
                        unselectedIcon = Icons.Outlined.Info,
                    ),
                    MenuItem(
                        id = 3,
                        tempId = "medData",
                        title = "Medizinische Daten",
                        selectedIcon = Icons.Filled.Info,
                        unselectedIcon = Icons.Outlined.Info,
                    ),
                    MenuItem(
                        id = 4,
                        tempId = "login",
                        title = "Logout",
                        selectedIcon = Icons.Filled.Person,
                        unselectedIcon = Icons.Outlined.Person,
                    ),
                ),
                onItemClick = {
                    if (it.id != 4) {
                        viewModel.menuSelectedItemIndex.value = it.id
                        navController.navigate("dashboard") // Update dashboard with animation
                    } else {
                        navController.navigate("login")
                        viewModel.menuSelectedItemIndex.value = 0 // Reset to Dashboard
                    }
                },
                viewModel = viewModel
            )
        }
    ) { contentPadding ->
        Column(
            Modifier
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
        ) {
            when (viewModel.menuSelectedItemIndex.value) {
                0 -> DashboardScreen(navController, viewModel, mainActivity,permissionsToRequest,promptManager)
                1 -> UserForm(viewModel, mainActivity)
                2 -> DoctorForm(viewModel, mainActivity)
                3 -> HealthForm(viewModel)
            }
        }
    }
}