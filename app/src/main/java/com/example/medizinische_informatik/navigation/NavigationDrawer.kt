package com.example.medizinische_informatik.navigation

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medizinische_informatik.ContactViewModel
import com.example.medizinische_informatik.R
import com.example.medizinische_informatik.db.Database.MenuItem
@Composable
fun DrawerHeader() {
    val configuration = LocalConfiguration.current
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Image(
                painter = painterResource(id = R.drawable.logo2),
                contentDescription = "logo",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun DrawerBody(
    items: List<MenuItem>,
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
    viewModel: ContactViewModel,
    onItemClick: (MenuItem) -> Unit
) {
    LazyColumn(modifier) {
        items(items) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(item) }
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = if (item.id == viewModel.menuSelectedItemIndex.value) item.selectedIcon else item.unselectedIcon,
                    contentDescription = item.title,
                    tint = if (item.id == viewModel.menuSelectedItemIndex.value) colorResource(id = R.color.skyblue3) else Color.Gray
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.title,
                    style = itemTextStyle,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
