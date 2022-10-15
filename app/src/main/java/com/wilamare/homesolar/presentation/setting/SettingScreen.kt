package com.wilamare.homesolar.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.wilamare.homesolar.domain.setting.Condition
import com.wilamare.homesolar.presentation.common.TopBar
import com.wilamare.homesolar.common.adjustBrightness
import com.wilamare.homesolar.presentation.destinations.ConditionScreenDestination
import com.wilamare.homesolar.ui.theme.PrimaryPurple
import com.wilamare.homesolar.ui.theme.PrimaryYellow

@Destination
@Composable
fun SettingScreen(
    navigator: DestinationsNavigator,
    viewModel: SettingViewModel = hiltViewModel()
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(
                title = "Settings",
                icon = Icons.Default.ArrowBack,
                contentDescription = "Go back to Dashboard",
                onNavigationIconClick = {
                    navigator.navigateUp()
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navigator.navigate(ConditionScreenDestination()) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Condition")
            }
        }
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(it)
                .padding(16.dp)
        ) {
            Text(text = "Parameter-Based Controls", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if(viewModel.state.value.conditions.isEmpty()){
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text(text = "No controls set, please add a new control using the add button")
                    }
                } else {
                    viewModel.state.value.conditions.forEach { condition ->
                        ConditionItem(
                            condition = condition,
                            onEdit = { navigator.navigate(ConditionScreenDestination(condition = condition)) },
                            onDelete = {}
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun ConditionItem(condition: Condition, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = condition.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Row {
                    IconButton(modifier = Modifier.size(16.dp), onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Condition"
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(modifier = Modifier.size(16.dp), onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Condition"
                        )
                    }
                }
            }
            Text(text = condition.description, fontSize = 14.sp, fontWeight = FontWeight.Light)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                TextWithBackground(color = PrimaryYellow.adjustBrightness(.9f), text = "IF")
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    condition.parameters.forEach { parameter ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextWithBackground(
                                color = PrimaryYellow.adjustBrightness(.9f),
                                text = parameter.measurement
                            )
                            TextWithBackground(
                                color = PrimaryYellow.adjustBrightness(.9f),
                                text = parameter.operator
                            )
                            TextWithBackground(
                                color = PrimaryYellow.adjustBrightness(.9f),
                                text = parameter.value
                            )
                            if (parameter.extra != "None") {
                                TextWithBackground(
                                    color = PrimaryYellow.adjustBrightness(.9f),
                                    text = parameter.extra
                                )
                            }
                        }

                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                TextWithBackground(color = PrimaryPurple, text = "Then")
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    condition.actions.forEach { action ->
                        TextWithBackground(color = PrimaryPurple, text = action.type)
                    }
                }
            }
        }
    }
}

@Composable
fun TextWithBackground(color: Color, text: String) {
    Text(
        modifier = Modifier
            .background(color)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp)),
        text = text,
        fontSize = 16.sp,
        overflow = TextOverflow.Ellipsis
    )
}