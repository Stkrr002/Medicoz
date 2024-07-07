package com.alpharays.medico.presentation.appointment_screen

import android.content.Context
import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DoneOutline
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alpharays.medico.R
import com.alpharays.medico.presentation.navigation.AppScreens
import com.alpharays.medico.medico_utils.MedicoConstants.DATE_OR_TIME
import com.alpharays.medico.medico_utils.MedicoConstants.PATIENT_AGE
import com.alpharays.medico.medico_utils.MedicoConstants.PATIENT_CONTACT
import com.alpharays.medico.medico_utils.MedicoConstants.PATIENT_DISEASE
import com.alpharays.medico.medico_utils.MedicoConstants.PATIENT_GENDER
import com.alpharays.medico.medico_utils.MedicoConstants.PATIENT_NAME
import com.alpharays.medico.medico_utils.MedicoToast
import com.alpharays.medico.medico_utils.MedicoUtils
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import java.util.Calendar

@Composable
fun AppointmentStartScreen(navController: NavController) {
    val context = LocalContext.current
    val parentAlpha by remember { mutableFloatStateOf(1f) }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .alpha(parentAlpha)
    ) {
        ComposableAppointmentStartScreen(context, navController, Modifier)
    }
}


@Composable
fun ComposableAppointmentStartScreen(
    context: Context,
    navController: NavController,
    modifier: Modifier
) {
    LazyColumn(
        modifier.fillMaxSize()
    ) {
        item {
            ComposableAppointmentUpperRow(navController, modifier)
        }
        item {
            ComposableAppointmentDetails(context, navController, modifier)
        }
    }
}


@Composable
fun ComposableAppointmentUpperRow(navController: NavController, modifier: Modifier) {
    Row(
        modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(start = 5.dp)
                .clickable {
                    navController.popBackStack()
                    navController.navigate(AppScreens.HomeScreen.route){
                        launchSingleTop = true
                    }
                },
            imageVector = Icons.Default.ArrowBackIos,
            contentDescription = "Go back"
        )

        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = "Appointment Schedule",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.W500
            )
        )
    }
}


@Composable
fun ComposableAppointmentDetails(
    context: Context,
    navController: NavController,
    modifier: Modifier
) {
    var onCompletePatientName by remember {
        mutableStateOf("")
    }
    var onCompletePatientDisease by remember {
        mutableStateOf("")
    }
    var onCompletePatientGender by remember {
        mutableStateOf("")
    }
    var onCompletePatientAge by remember {
        mutableStateOf("")
    }
    var onCompletePatientDateTime by remember {
        mutableStateOf("")
    }
    var onCompletePatientContact by remember {
        mutableStateOf("")
    }

    var patientName by remember {
        mutableStateOf("")
    }
    var patientDisease by remember {
        mutableStateOf("")
    }
    var patientContact by remember {
        mutableStateOf("")
    }
    val color = MedicoUtils.getMedicoColor(context, R.color.bluish_gray)
    val customButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFFB1BDFF),
        contentColor = Color(color),
    )
    val patientDetailsMap = remember {
        mutableStateMapOf(
            PATIENT_NAME to "",
            PATIENT_DISEASE to "",
            PATIENT_AGE to "",
            PATIENT_CONTACT to "",
            PATIENT_GENDER to "",
            DATE_OR_TIME to "",
        )
    }

    Column(
        modifier.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ComposableNameTextField(
            "Name",
            patientName,
            "Enter your name",
        ) { name ->
            patientName = name
            onCompletePatientName = name
            patientDetailsMap[PATIENT_NAME] = onCompletePatientName
        }

        ComposableNameTextField(
            "Disease/Problem",
            patientDisease,
            "Enter problem(s) faced"
        ) { disease ->
            patientDisease = disease
            onCompletePatientDisease = disease
            patientDetailsMap[PATIENT_DISEASE] = onCompletePatientDisease
        }

        ComposableAgeGenderField { ageGender ->
            val (gender, age) = ageGender.split("$")
            onCompletePatientGender = gender
            onCompletePatientAge = age
            patientDetailsMap[PATIENT_AGE] = onCompletePatientAge
            patientDetailsMap[PATIENT_GENDER] = onCompletePatientGender
        }

        ComposableAppointmentDateTime { dateTime ->
            val (date, time) = dateTime.split("$")
            onCompletePatientDateTime = "$date\n$time"
            patientDetailsMap[DATE_OR_TIME] = onCompletePatientDateTime
        }

        ComposableNameTextField(
            "Contact Number",
            patientContact,
            "Enter your contact",
            numbersOnly = true
        ) { number ->
            patientContact = number
            onCompletePatientContact = number
            patientDetailsMap[PATIENT_CONTACT] = onCompletePatientContact
        }

        OutlinedButton(
            modifier = Modifier.padding(20.dp),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(0.dp, Color.White),
            colors = customButtonColors,
            onClick = {
                patientDetailsMap.forEach { (key, value) ->
                    if (value.isEmpty()) {
                        MedicoToast.showToast(context, "$key can not be empty")
                        return@OutlinedButton
                    }
                }
                MedicoToast.showToast(context, "Appointment scheduled for $onCompletePatientName")
            }) {
            Row(
                modifier = Modifier.padding(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(1.dp),
                    text = "Submit",
                    fontSize = 16.sp
                )
                Icon(
                    modifier = Modifier.padding(1.dp),
                    imageVector = Icons.Default.DoneOutline,
                    contentDescription = "Date and time"
                )
            }
        }
    }
}


@Composable
fun ComposableNameTextField(
    heading: String,
    textField: String,
    fieldText: String,
    numbersOnly: Boolean = false,
    ageField: Boolean = false,
    onValueChange: (String) -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 5.dp)
    ) {
        if (heading.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(bottom = 5.dp),
                text = heading,
                fontSize = 18.sp
            )
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = textField,
            onValueChange = { newText ->
                if (numbersOnly) {
                    if (newText.length <= 10) {
                        onValueChange(newText)
                        if (newText.length == 10) {
                            keyboardController?.hide()
//                            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                            imm.hideSoftInputFromWindow(view.windowToken, 0)
                        }
                    }
                } else if (ageField) {
                    if (newText.length <= 2) {
                        if (newText.isNotEmpty() && (newText.toInt() < 1 || newText.toInt() > 99)) {
                            MedicoToast.showToast(context, "Enter valid age")
                        } else {
                            onValueChange(newText)
                            if (newText.length == 2) {
                                keyboardController?.hide()
                            }
                        }
                    }
                } else {
                    onValueChange(newText)
                }
            },
            prefix = {
                if (numbersOnly) {
                    Icon(
                        modifier = Modifier.padding(end = 10.dp),
                        imageVector = Icons.Default.Call,
                        contentDescription = "Contact"
                    )
                }
            },
            label = {
                Text(text = fieldText)
            },
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color(0xFF00897B)
            ),
            keyboardOptions = if (numbersOnly || ageField) {
                KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            } else {
                KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Ascii,
                    imeAction = ImeAction.Done
                )
            }
        )
    }
}


@Composable
fun keyboardAsState(): State<Keyboard> {
    val keyboardState = remember { mutableStateOf(Keyboard.Closed) }
    val view = LocalView.current
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardState.value = if (keypadHeight > screenHeight * 0.3) {
                Keyboard.Opened
            } else {
                Keyboard.Closed
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    return keyboardState
}


@Composable
fun ComposableAgeGenderField(onFieldCompleteValue: (String) -> Unit) {
    var patientAge by remember {
        mutableStateOf("")
    }
    val ageRadioOptions = listOf("Male", "Female")
    var selectedRadioOption by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 5.dp)
    ) {
        Text(text = "Age and Gender", fontSize = 18.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            ageRadioOptions.forEach { gender ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 5.dp)
                ) {
                    RadioButton(
                        selected = gender == selectedRadioOption,
                        onClick = {
                            selectedRadioOption = gender
                        }
                    )
                    Text(
                        text = gender,
                        fontSize = 15.sp
                    )
                }
            }

            ComposableNameTextField("", patientAge, "Enter age", ageField = true) { age ->
                patientAge = age
            }
        }
    }
    onFieldCompleteValue("$selectedRadioOption$$patientAge")
}


@Composable
fun ComposableAppointmentDateTime(onFieldCompleteValue: (String) -> Unit) {
    var showDateTimePopUp by remember {
        mutableStateOf(false)
    }
    val isKeyboardOpen by keyboardAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "Select Date and Time",
            fontSize = 18.sp
        )

        OutlinedButton(
            modifier = Modifier.weight(0.5f),
            shape = RoundedCornerShape(10.dp),
            onClick = {
                showDateTimePopUp = !showDateTimePopUp
            }) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Date and time"
            )
        }
    }

    if (showDateTimePopUp) {
        if (isKeyboardOpen == Keyboard.Opened) {
            keyboardController?.hide()
        }
        ComposableDateTimePopup(
            onDismiss = {
                showDateTimePopUp = false
            },
            onFieldCompleteValue = { dateTime ->
                if (isKeyboardOpen == Keyboard.Opened) {
                    keyboardController?.hide()
                }
                onFieldCompleteValue(dateTime)
            }
        )
    }
}


@Composable
fun ComposableDateTimePopup(onDismiss: () -> Unit, onFieldCompleteValue: (String) -> Unit) {
    Popup(
        alignment = Alignment.TopCenter,
        onDismissRequest = { onDismiss() },
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            SelectableDateTimePopUp(Modifier) { dateTime ->
                onFieldCompleteValue(dateTime)
            }
        }
    }
}


@Composable
fun SelectableDateTimePopUp(modifier: Modifier, onFieldCompleteValue: (String) -> Unit) {
    val calendarState = rememberSelectableCalendarState()
    val selectionState = calendarState.selectionState
    var selectDateTimeText by remember {
        mutableStateOf("")
    }
    var dateSelected by remember {
        mutableStateOf("")
    }
    var timeSelected by remember {
        mutableStateOf("")
    }
    var nextClickSelectTime by remember {
        mutableStateOf(false)
    }

    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFD4EFFF), RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .padding(horizontal = 6.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            selectDateTimeText = if (nextClickSelectTime) "Select time" else "Select date"

            Text(
                modifier = Modifier.padding(10.dp).weight(1f),
                text = selectDateTimeText,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W400
                ),
                color = Color(0xFF1F0600)
            )

            Icon(
                imageVector = Icons.Default.NavigateNext,
                contentDescription = "Next item",
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        nextClickSelectTime = !nextClickSelectTime
                    }
            )
        }

        Crossfade(
            targetState = nextClickSelectTime,
            animationSpec = tween(durationMillis = 600, easing = FastOutLinearInEasing),
            label = ""
        ) { showTimePicker ->
            if (!showTimePicker) {
                AnimatedVisibility(visible = true) {
                    Column {
                        SelectableCalendar(calendarState = calendarState)
                        dateSelected = selectionState.selection.joinToString { it.toString() }
                        val text = "Selection: $dateSelected"
                        Text(
                            modifier = Modifier.padding(10.dp),
                            text = text
                        )
                    }
                }
            } else {
                AnimatedVisibility(visible = true) {
                    ComposableAvailableTimeSelect { time ->
                        timeSelected = time
                    }
//                    ComposableTimeSelect()
                }
            }
        }

        if (dateSelected.isNotEmpty() && timeSelected.isNotEmpty()) {
            onFieldCompleteValue("$dateSelected$$timeSelected")
        }

//        SelectionControls(selectionState = calendarState.selectionState)
    }
}


@Composable
fun ComposableAvailableTimeSelect(onFieldCompleteValue: (String) -> Unit) {
    val availableTimeRadioOptions =
        listOf("07:30 AM", "10:00 AM", "12:30 PM", "02:30 PM", "05:00 PM", "07:00 PM")
    var selectedRadioOption by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
        for (i in availableTimeRadioOptions.indices step 2) {
            ComposableTimeRowRadioOptions(
                availableTimeRadioOptions.subList(i, i + 2),
                selectedRadioOption,
                onOptionSelected = { selectedOption ->
                    selectedRadioOption = selectedOption
                    onFieldCompleteValue(selectedOption)
                }
            )
        }
    }
}


@Composable
fun ComposableTimeRowRadioOptions(
    availableTimeRadioOptions: List<String>,
    selectedRadioOption: String,
    onOptionSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        availableTimeRadioOptions.forEach { time ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(5.dp)
            ) {
                RadioButton(
                    selected = time == selectedRadioOption,
                    onClick = {
                        onOptionSelected(time)
                    }
                )
                Text(
                    modifier = Modifier.padding(start = 5.dp),
                    text = time,
                    fontSize = 18.sp
                )
            }
        }
    }
}


fun getCurrentTime(): Pair<Int, Int> {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY) // 24-hour format
    val minute = calendar.get(Calendar.MINUTE)
    return Pair(hour, minute)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposableTimeSelect() {
    val (initialHour, initialMinute) = getCurrentTime()
    val timeState = rememberTimePickerState(initialHour, initialMinute, false)
    val scaleFactor = 0.8f
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
//    val customTimePickerColors = TimePickerDefaults.colors() : IMP
        TimePicker(
            modifier = Modifier.graphicsLayer(
                scaleX = scaleFactor,
                scaleY = scaleFactor
            ),
            state = timeState
        )
        Text(text = "Time is ${timeState.hour} : ${timeState.minute}")
    }
}


@Composable
private fun SelectionControls(
    selectionState: DynamicSelectionState,
) {
    Text(
        text = "Calendar Selection Mode",
        style = MaterialTheme.typography.bodySmall,
    )
    SelectionMode.entries.forEach { selectionMode ->
        Row(modifier = Modifier.fillMaxWidth()) {
            RadioButton(
                selected = selectionState.selectionMode == selectionMode,
                onClick = { selectionState.selectionMode = selectionMode }
            )
            Text(text = selectionMode.name)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }

    Text(
        text = "Selection: ${selectionState.selection.joinToString { it.toString() }}",
        style = MaterialTheme.typography.bodySmall,
    )
}


enum class Keyboard {
    Opened, Closed
}


@Preview
@Composable
fun ComposeAppointmentCalendarViewPreview() {
    val navController = rememberNavController()
    AppointmentStartScreen(navController)
}