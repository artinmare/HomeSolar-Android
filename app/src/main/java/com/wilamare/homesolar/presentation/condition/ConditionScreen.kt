package com.wilamare.homesolar.presentation.condition

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.wilamare.homesolar.domain.setting.Condition
import com.wilamare.homesolar.presentation.common.Menu
import com.wilamare.homesolar.presentation.common.TopBar
import com.wilamare.homesolar.ui.theme.PrimaryPurple
import com.wilamare.homesolar.ui.theme.PrimaryYellow

@OptIn(ExperimentalAnimationApi::class)
@Destination
@Composable
fun ConditionScreen(
    navigator: DestinationsNavigator,
    condition: Condition = Condition(),
    viewModel: ConditionViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    var dialog by remember { mutableStateOf(false) }

    LaunchedEffect(condition) {
        viewModel.setCondition(condition)
    }

    if (dialog) {
        AlertDialog(
            onDismissRequest = { dialog = false },
            title = { Text(text = "Do you want to save the edit ?") },
            text = { Text(text = "Note: Invalid input would be discarded, only valid input would be saved!")},
            buttons = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { dialog = false }) {
                        Text(text = "Cancel")
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(onClick = {
                            dialog = false
                            navigator.navigateUp()
                        }) {
                            Text(text = "Discard")
                        }
                        TextButton(onClick = {
                            dialog = false
                            Log.d("ConditionScreen", "Condition: ${viewModel.validateCondition()}")
                            navigator.navigateUp()
                        }) {
                            Text(text = "Save")
                        }
                    }
                }
            })
    }
    BackHandler(enabled = dialog.not()) {
        dialog = true
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        topBar = {
            TopBar(
                title = "Condition Edit",
                icon = Icons.Default.ArrowBack,
                contentDescription = "Go back to Settings",
                onNavigationIconClick = {
                    dialog = true
                }
            )
        }
    ) { paddingValues ->

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.state.value.condition.name,
                singleLine = true,
                onValueChange = { viewModel.onNameChange(it) },
                textStyle = TextStyle(
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                ),
                label = {
                    Text(text = "Name")
                }
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.state.value.condition.description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                textStyle = TextStyle(
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                ),
                label = {
                    Text(text = "Description")
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Parameter")
            viewModel.state.value.condition.parameters.forEachIndexed { index, parameter ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val (measurementMenuIsOpen, setMeasurementMenuIsOpen) = remember {
                            mutableStateOf(false)
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    PrimaryYellow
                                )
                                .clickable { setMeasurementMenuIsOpen(true) }
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                        ) {
                            Text(
                                text = parameter.measurement,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Menu(
                            modifier = Modifier.heightIn(max = 500.dp),
                            menu = viewModel.getMenu(DropdownMenu.MEASUREMENT),
                            offset = DpOffset(8.dp, 0.dp),
                            isOpen = measurementMenuIsOpen,
                            setIsOpen = setMeasurementMenuIsOpen,
                            itemSelected = {
                                setMeasurementMenuIsOpen(false)
                                viewModel.onMeasurementSelected(selected = it, index = index)
                            })

                        val (operatorMenuIsOpen, setOperatorMenuIsOpen) = remember {
                            mutableStateOf(false)
                        }
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    PrimaryYellow
                                )
                                .clickable { setOperatorMenuIsOpen(true) }
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                        ) {
                            Text(
                                text = parameter.operator,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Menu(
                            modifier = Modifier.heightIn(max = 500.dp),
                            menu = viewModel.getMenu(DropdownMenu.OPERATOR, index = index),
                            offset = DpOffset(500.dp, 0.dp),
                            isOpen = operatorMenuIsOpen,
                            setIsOpen = setOperatorMenuIsOpen,
                            itemSelected = {
                                setOperatorMenuIsOpen(false)
                                viewModel.updateParameter(index = index, operator = it)
                            })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            value = parameter.value,
                            onValueChange = {
                                viewModel.onValueChange(index = index, value = it)
                            },
                            label = {
                                Text(text = "Value")
                            },
                            keyboardOptions = if(parameter.type == DataType.NUMBER) KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number) else KeyboardOptions.Default
                        )

                        val (extraMenuIsOpen, setExtraMenuIsOpen) = remember { mutableStateOf(false) }
                        Button(onClick = { setExtraMenuIsOpen(true) }) {
                            Text(text = parameter.extra)
                        }
                        Menu(
                            modifier = Modifier.heightIn(max = 500.dp),
                            menu = viewModel.getMenu(DropdownMenu.EXTRA, index = index),
                            offset = DpOffset(500.dp, 0.dp),
                            isOpen = extraMenuIsOpen,
                            setIsOpen = setExtraMenuIsOpen,
                            itemSelected = {
                                setExtraMenuIsOpen(false)
                                viewModel.onExtraMenuSelected(extra = it,index = index)
                            })
                    }

                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            if(viewModel.state.value.condition.parameters.isEmpty()){
                IconButton(onClick = { viewModel.addParameter() }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Parameter")
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Actions")
            viewModel.state.value.condition.actions.forEachIndexed { index, action ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val (actionMenuIsOpen, setActionMenuIsOpen) = remember { mutableStateOf(false) }
                    val padding = if(action.type == "MQTT") 8.dp else 0.dp
                    Column(
                        modifier = Modifier
                            .padding(top = padding)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                PrimaryPurple
                            )
                            .clickable { setActionMenuIsOpen(true) }
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        Text(
                            text = action.type,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            overflow = TextOverflow.Ellipsis
                        )

                    }
                    Menu(
                        modifier = Modifier.heightIn(max = 500.dp),
                        menu = viewModel.getMenu(DropdownMenu.ACTION),
                        offset = DpOffset(0.dp, 0.dp),
                        isOpen = actionMenuIsOpen,
                        setIsOpen = setActionMenuIsOpen,
                        itemSelected = {
                            setActionMenuIsOpen(false)
                            viewModel.onActionMenuSelected(index = index, type = it)
                        })
                    if(action.type == "GPIO"){
                        val (gpioMenuIsOpen, setGpioMenuIsOpen) = remember { mutableStateOf(false) }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    PrimaryPurple
                                )
                                .clickable { setGpioMenuIsOpen(true) }
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                        ) {
                            Text(
                                text = action.gpio,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                overflow = TextOverflow.Ellipsis
                            )

                        }
                        Menu(
                            modifier = Modifier.heightIn(max = 500.dp),
                            menu = viewModel.getMenu(DropdownMenu.GPIO),
                            offset = DpOffset(50.dp, 0.dp),
                            isOpen = gpioMenuIsOpen,
                            setIsOpen = setGpioMenuIsOpen,
                            itemSelected = {
                                setGpioMenuIsOpen(false)
                                viewModel.updateAction(index = index, gpio = it)
                            })

                        val (gpioActionMenuIsOpen, setGpioActionMenuIsOpen) = remember { mutableStateOf(false) }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    PrimaryPurple
                                )
                                .clickable { setGpioActionMenuIsOpen(true) }
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                        ) {
                            Text(
                                text = action.gpioAction,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                overflow = TextOverflow.Ellipsis
                            )

                        }
                        Menu(
                            modifier = Modifier.heightIn(max = 500.dp),
                            menu = viewModel.getMenu(DropdownMenu.GPIO_ACTION),
                            offset = DpOffset(500.dp, 0.dp),
                            isOpen = gpioActionMenuIsOpen,
                            setIsOpen = setGpioActionMenuIsOpen,
                            itemSelected = {
                                setGpioActionMenuIsOpen(false)
                                viewModel.updateAction(index = index, gpioAction = it)
                            })
                    } else {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedTextField(modifier = Modifier.fillMaxWidth(), singleLine = true, value = action.topic, onValueChange = {viewModel.updateAction(index = index, topic = it)}, label = { Text(
                                text = "Topic"
                            )})

                            OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = action.payload, onValueChange = {viewModel.updateAction(index = index, payload = it)}, label = { Text(
                                text = "Payload"
                            )})
                        }
                    }
                }
            }
            IconButton(onClick = { viewModel.addAction() }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Action")
            }
        }
    }
}


