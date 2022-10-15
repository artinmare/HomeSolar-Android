package com.wilamare.homesolar.presentation.about

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.wilamare.homesolar.presentation.common.TopBar
import com.wilamare.homesolar.presentation.destinations.HomeScreenDestination
import kotlinx.coroutines.launch

@Destination
@Composable
fun AboutScreen(
    navigator: DestinationsNavigator
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(
                title = "About",
                icon = Icons.Default.ArrowBack,
                contentDescription = "Go back to Dashboard",
                onNavigationIconClick = {
                    scope.launch {
                        navigator.navigateUp()
                    }
                }
            )
        }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "This app is part of the Final Project for my Electrical Engineering Degree on Nusa Cendana University. Thank you for using the app!")
                Text(text = "You can find the source code for this project on my Github using the button below.")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Regards,\nFernando Martin Wila Mare\n1706030056")
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(modifier = Modifier.fillMaxWidth(.8f), onClick = { /*TODO*/ }) {
                Text(text = "Open Github")
            }


        }
    }
}