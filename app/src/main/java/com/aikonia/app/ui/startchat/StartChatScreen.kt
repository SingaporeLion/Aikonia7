package com.aikonia.app.ui.startchat

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aikonia.app.R
import com.aikonia.app.common.components.AnimatedButton
import com.aikonia.app.common.components.AppBar
import com.aikonia.app.common.components.NoConnectionDialog
import com.aikonia.app.common.components.ThereIsUpdateDialog
import com.aikonia.app.ui.activity.isOnline
import com.aikonia.app.ui.theme.Green
import com.aikonia.app.ui.theme.GreenShadow
import com.aikonia.app.ui.theme.Urbanist
// import com.aikonia.app.ui.upgrade.PurchaseHelper
import kotlinx.coroutines.delay
import java.util.Locale
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
//import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import android.content.SharedPreferences

@Composable
fun StartChatScreen(
    navigateToMenu: () -> Unit,
    navigateToChat: (String, String, List<String>?) -> Unit,
    startChatViewModel: StartChatViewModel = hiltViewModel(),
    sharedPreferences: SharedPreferences // Bereitstellen der SharedPreferences-Instanz hier
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var birthYear by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Mädchen", "Junge")
    val shouldStartInMenu = sharedPreferences.getBoolean("shouldStartInMenu", true)
    val isUserDataSaved = startChatViewModel.isUserDataSaved.collectAsState().value

    fun changeLanguage(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    LaunchedEffect(Unit) {
        if (shouldStartInMenu) {
            navigateToMenu()
        } else {
            startChatViewModel.checkUserDataExists(1) // Beispiel-UserID
            startChatViewModel.getCurrentLanguageCode()
            changeLanguage(startChatViewModel.currentLanguageCode.value)
            startChatViewModel.getFirstTime()
            startChatViewModel.getProVersion()
            startChatViewModel.isThereUpdate()
        }

        if (isOnline(context).not()) showDialog = true
        if (startChatViewModel.isThereUpdate.value) showUpdateDialog = true
    }

    if (!shouldStartInMenu && isUserDataSaved) {
        navigateToChat(name, birthYear, listOf(gender))
    }


    if (showDialog) {
        NoConnectionDialog {
            showDialog = false
        }
    }

    if (showUpdateDialog) {
        ThereIsUpdateDialog {
            try {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.aikonia.app")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.aikonia.app")
                    )
                )
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppBar(
            onClickAction = {},
            image = R.drawable.app_icon,
            text = stringResource(R.string.app_name),
            Green
        )
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // UI-Elemente für Eingabefelder
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") }
                // Weitere Eigenschaften...
            )
            OutlinedTextField(
                value = birthYear,
                onValueChange = { birthYear = it },
                label = { Text("Geburtsjahr") }
                // Weitere Eigenschaften...
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = { /* Nichts tun, da die Auswahl über das Dropdown-Menü erfolgt */ },
                    label = { Text("Geschlecht") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Filled.ArrowDropDown, "Dropdown-Icon", modifier = Modifier.clickable { expanded = !expanded })
                    },
                    modifier = Modifier
                        .clickable { expanded = !expanded }
                        .align(Alignment.Center)
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    genderOptions.forEach { option ->
                        DropdownMenuItem(onClick = {
                            gender = option
                            expanded = false
                        }) {
                            Text(option, color = Color.Black) // Setzt die Textfarbe explizit auf Schwarz
                        }
                    }
                }

            }

            //Icon(
            //    painter = painterResource(R.drawable.app_icon),
            //    contentDescription = stringResource(R.string.app_name),
            //  tint = Green,
            //  modifier = Modifier.size(200.dp)
            //)
            Spacer(modifier = Modifier.height(30.dp))
            if (startChatViewModel.isProVersion.value.not()) {
                Text(
                    text = stringResource(R.string.welcome_to),
                    color = MaterialTheme.colors.surface,
                    style = TextStyle(
                        fontSize = 40.sp,
                        fontWeight = FontWeight.W700,
                        fontFamily = Urbanist,
                        lineHeight = 25.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.welcome_app_name),
                    color = Green,
                    style = TextStyle(
                        fontSize = 40.sp,
                        fontWeight = FontWeight.W700,
                        fontFamily = Urbanist,
                        lineHeight = 25.sp
                    ),
                    textAlign = TextAlign.Center
                )
            } else {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        color = MaterialTheme.colors.surface,
                        style = TextStyle(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.W700,
                            fontFamily = Urbanist,
                            lineHeight = 25.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = stringResource(R.string.pro),
                        color = MaterialTheme.colors.primary,
                        style = TextStyle(
                            fontSize = 25.sp,
                            fontWeight = FontWeight.W700,
                            fontFamily = Urbanist,
                            lineHeight = 25.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .background(GreenShadow, shape = RoundedCornerShape(90.dp))
                            .padding(horizontal = 9.dp)
                    )

                }
            }

            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = stringResource(R.string.welcome_description),
                color = MaterialTheme.colors.surface,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W500,
                    fontFamily = Urbanist,
                    lineHeight = 25.sp
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(40.dp))

            // Start-Chat-Button
            AnimatedButton(onClick = {
                if (name.isNotEmpty() && birthYear.isNotEmpty() && gender.isNotEmpty()) {
                    startChatViewModel.saveUser(name, birthYear, gender)
                } else {
                    // Warnung, dass alle Felder ausgefüllt werden müssen
                }
            }, text = stringResource(R.string.start_chat))

            // Navigation zum Chat bei erfolgreicher Speicherung der Benutzerdaten
            val isSaved = startChatViewModel.isUserDataSaved.collectAsState().value
            if (isSaved) navigateToChat(name, birthYear, listOf(gender))


        }
    }
}


