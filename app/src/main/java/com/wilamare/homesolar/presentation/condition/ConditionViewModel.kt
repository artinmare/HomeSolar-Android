package com.wilamare.homesolar.presentation.condition

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.DeleteSweep
import androidx.compose.material.icons.twotone.Done
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.androidpoet.dropdown.MenuItem
import com.androidpoet.dropdown.dropDownMenu
import com.wilamare.homesolar.common.addCharAtIndex
import com.wilamare.homesolar.domain.setting.Action
import com.wilamare.homesolar.domain.setting.Condition
import com.wilamare.homesolar.domain.setting.Parameter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class ConditionViewModel @Inject constructor(

) : ViewModel() {
    sealed class UIEvent {
        data class ShowSnackbar(val message: String) : UIEvent()
    }

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _state = mutableStateOf(ConditionState())
    val state: State<ConditionState> = _state

    fun validateCondition(): Boolean{
        val condition = state.value.condition
        Log.d("ConditionViewModel", "Condition: $condition")
        if(condition.name == ""){
            return false
        }
        //validate parameters
        condition.parameters.forEachIndexed { index, parameter ->
            if(!validateParameter(parameter)){
                updateParameter(index = index, valid = false)
            }
        }
        //validate actions
        condition.actions.forEachIndexed { index, action ->
            if(!validateAction(action)){
                updateAction(index = index, valid = false)
            }
        }

        //updateList
        val newParameters = state.value.condition.parameters.filter { it.valid }
        val newActions = state.value.condition.actions.filter { it.valid }

        setCondition(parameters = newParameters, actions = newActions)

        Log.d("ConditionViewModel", "Validated Condition: ${state.value.condition}")
        if(newParameters.isEmpty() || newActions.isEmpty()){
            return false
        }
        return true
    }

    private fun validateParameter(parameter: Parameter): Boolean{
        if(parameter.measurement == "Measurement#Field"){
            return false
        }
        if(parameter.value == ""){
            return false
        }
        if(parameter.type == DataType.NUMBER && parameter.value.toDoubleOrNull() == null){
            return false
        }
        return true
    }

    private fun validateAction(action: Action): Boolean{
        if(action.type == "MQTT" && (action.topic == "" || action.payload == "")){
            return false
        }
        if(action.type == "GPIO" && (action.gpio == "Type#Pin" || action.gpioAction == "None")){
            return false
        }
        return true
    }

    fun setCondition(condition: Condition) {
        _state.value = state.value.copy(condition = condition)
    }

    private fun setCondition(
        name: String? = null,
        description: String? = null,
        parameters: List<Parameter>? = null,
        actions: List<Action>? = null
    ) {
        name?.let {
            _state.value = state.value.copy(condition = state.value.condition.copy(name = it))
        }
        description?.let {
            _state.value =
                state.value.copy(condition = state.value.condition.copy(description = it))
        }
        parameters?.let {
            _state.value = state.value.copy(condition = state.value.condition.copy(parameters = it))
        }
        actions?.let {
            _state.value = state.value.copy(condition = state.value.condition.copy(actions = it))
        }
    }

    fun onNameChange(name: String) {
        setCondition(name = name)
    }

    fun onDescriptionChange(description: String) {
        setCondition(description = description)
    }

    fun onValueChange(index: Int, value: String){
        val newValue = if (state.value.condition.parameters[index].type == DataType.NUMBER) validateNumber(value) else value
        updateParameter(index = index, value = newValue)
    }

    private fun validateNumber(value: String): String{
        var newValue = value.filter { it.isDigit() || it == '.' || it == '-' }.trimStart { it == '.' }
        if(newValue.length > 1 && newValue.first() == '0' && newValue[1] != '.'){
            newValue = newValue.take(1)
        }
        if(newValue.length > 2 && newValue.first() == '-' && newValue[1] == '0' && newValue[2] != '.'){
            newValue = newValue.take(2)
        }
        if(newValue.count { it == '.'} > 1){
            newValue = newValue.dropLast(1)
        }
        if(newValue.length > 1 && newValue.last() == '-'){
            newValue = newValue.dropLast(1)
        }
        return newValue

    }

    fun addParameter(index: Int? = null) {
        val newParameters = state.value.condition.parameters.toMutableList()
        var valid = true

        index?.let {
            valid = it == newParameters.lastIndex
        }

        if(valid){
            newParameters.add(Parameter())
            setCondition(parameters = newParameters)
        }
    }

    fun addAction() {
        val newActions = state.value.condition.actions.toMutableList()
        newActions.add(Action())
        setCondition(actions = newActions)
    }

    fun updateParameter(
        index: Int,
        measurement: String? = null,
        dataType: DataType? = null,
        operator: String? = null,
        value: String? = null,
        extra: String? = null,
        valid: Boolean? = null
    ) {
        val newList = state.value.condition.parameters.toMutableList()
        var newParameter = newList[index]
        measurement?.let {
            newParameter = newParameter.copy(measurement=it)
        }
        dataType?.let {
            newParameter = newParameter.copy(type = it)
        }
        operator?.let {
            newParameter = newParameter.copy(operator=it)
        }
        value?.let {
            newParameter = newParameter.copy(value=it)
        }
        extra?.let {
            newParameter = newParameter.copy(extra=it)
        }
        valid?.let {
            newParameter = newParameter.copy(valid=it)
        }
        newList[index] = newParameter
        setCondition(parameters = newList)
    }

    fun onMeasurementSelected(selected: String, index: Int){
        val measurement = selected.split("#")
        val fieldType = state.value.measurements.find { it.name == measurement[0] }?.fields?.find { it.name == measurement[1]}?.type
        val operator = if (fieldType == DataType.STRING) "==" else null
        val value = if (fieldType == DataType.NUMBER) "" else null
        updateParameter(index = index, measurement = selected, dataType = fieldType, operator = operator, value = value)
    }

    fun onExtraMenuSelected(extra:String, index: Int){
        when (extra){
            "yes" ->
                onDeleteParameter(index)
            "no"->
                {}
            "AND" ->
                {
                    updateParameter(index = index, extra = extra)
                    addParameter(index = index)
                }
            "OR" ->
                {
                    updateParameter(index = index, extra = extra)
                    addParameter(index = index)
                }
            else ->
                updateParameter(index = index, extra = extra)
        }
    }

    fun onActionMenuSelected(type: String, index: Int){
        when (type){
            "yes" ->
                onDeleteAction(index)
            "no"->
                {}
            else ->
                updateAction(index, type = type)
        }
    }

    private fun onDeleteParameter(index: Int){
        val newList = state.value.condition.parameters.toMutableList()
        newList.removeAt(index)
        if(newList.isNotEmpty()){
            newList[newList.lastIndex] = newList.last().copy(extra = "None")
        }
        setCondition(parameters = newList)
    }

    private fun onDeleteAction(index: Int){
        val newList = state.value.condition.actions.toMutableList()
        newList.removeAt(index)
        setCondition(actions = newList)
    }

    fun updateAction(
        index: Int,
        type:String? = null,
        topic:String? = null,
        payload:String? = null,
        gpio:String? = null,
        gpioAction:String? = null,
        valid: Boolean? = null
    ) {
        val newList = state.value.condition.actions.toMutableList()
        var newAction = newList[index]
        type?.let {
            newAction = newAction.copy(type = it)
        }
        topic?.let {
            newAction = newAction.copy(topic = it)
        }
        payload?.let {
            newAction = newAction.copy(payload = it)
        }
        gpio?.let {
            newAction = newAction.copy(gpio = it)
        }
        gpioAction?.let {
            newAction = newAction.copy(gpioAction = it)
        }
        valid?.let {
            newAction = newAction.copy(valid = it)
        }
        newList[index] = newAction
        setCondition(actions = newList)
    }

    private fun measurementMenu(): MenuItem<String> {
        val menu = dropDownMenu<String> {
            if (state.value.measurements.isEmpty()) {
                item("Measurement#Field", "No measurement detected")
            } else {
                state.value.measurements.forEach { measurement ->
                    item(measurement.name, measurement.name) {
                        measurement.fields.forEach { field ->
                            item("${measurement.name}#${field.name}", field.name)
                        }
                    }
                }
            }
        }
        return menu
    }

    private fun operatorMenu(index: Int? = null): MenuItem<String> {
        val menu = dropDownMenu<String> {
            item("!=", "(!=) Not Equal")
            item("==", "(==) Equal")
            index?.let {
                if(state.value.condition.parameters[it].type == DataType.NUMBER){
                    item(">", "(>)  Larger Than")
                    item(">=", "(>=) Larger or Equal to")
                    item("<", "(<)  Smaller Than")
                    item("<=", "(<=) Smaller or Equal to")
                }
            }
        }
        return menu
    }

    private fun extraMenu(index: Int? = null): MenuItem<String> {
        val menu = dropDownMenu<String> {
            if(state.value.condition.parameters.lastIndex == index){
                item("None", "None")
            }
            item("AND", "AND")
            item("OR", "OR")
            item("delete", "Delete ?") {
                icon(Icons.TwoTone.DeleteSweep)
                item("yes", "Yes") {
                    icon(Icons.TwoTone.Done)
                }
                item("no", "No") {
                    icon(Icons.TwoTone.Close)
                }
            }
        }
        return menu
    }
    private fun actionMenu(): MenuItem<String> {
        val menu = dropDownMenu<String> {
            item("GPIO", "GPIO")
            item("MQTT", "MQTT")
            item("delete", "Delete ?") {
                icon(Icons.TwoTone.DeleteSweep)
                item("yes", "Yes") {
                    icon(Icons.TwoTone.Done)
                }
                item("no", "No") {
                    icon(Icons.TwoTone.Close)
                }
            }
        }
        return menu
    }
    private fun gpioMenu(): MenuItem<String> {
        val menu = dropDownMenu<String> {
            item("Board", "Physical/Board"){
                item("Board#7", "7")
                item("Board#11", "11")
                item("Board#13", "13")
                item("Board#15", "15")
                item("Board#16", "16")
                item("Board#18", "18")
                item("Board#22", "22")
                item("Board#29", "29")
                item("Board#31", "31")
                item("Board#32", "32")
                item("Board#33", "33")
                item("Board#36", "36")
                item("Board#37", "37")
            }
            item("BCM", "GPIO/BCM"){
                item("GPIO#4", "4")
                item("GPIO#5", "5")
                item("GPIO#6", "6")
                item("GPIO#12", "12")
                item("GPIO#13", "13")
                item("GPIO#16", "16")
                item("GPIO#17", "17")
                item("GPIO#22", "22")
                item("GPIO#23", "23")
                item("GPIO#24", "24")
                item("GPIO#25", "25")
                item("GPIO#26", "26")
                item("GPIO#27", "27")
            }
        }
        return menu
    }

    private fun gpioActionMenu(): MenuItem<String> {
        val menu = dropDownMenu<String> {
            item("ON", "Turn ON")
            item("OFF", "Turn OFF")
            item("Toggle", "Toggle")
        }
        return menu
    }

    fun getMenu(
        dropdownMenu: DropdownMenu,
        index: Int? = null
    ): MenuItem<String> {
        return when (dropdownMenu) {
            DropdownMenu.MEASUREMENT ->
                measurementMenu()
            DropdownMenu.OPERATOR ->
                operatorMenu(index = index)
            DropdownMenu.EXTRA ->
                extraMenu(index = index)
            DropdownMenu.ACTION ->
                actionMenu()
            DropdownMenu.GPIO ->
                gpioMenu()
            DropdownMenu.GPIO_ACTION ->
                gpioActionMenu()
        }
    }
}