package com.alpharays.medico.presentation.home_screen


import android.content.Context
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.alpharays.medico.MedicoApp
import com.alpharays.medico.R
import com.alpharays.medico.domain.model.homescreen.currappointment.Appointment
import com.alpharays.medico.presentation.navigation.AppScreens
import com.alpharays.medico.medico_utils.MedicoConstants.NO_CONNECTION
import com.alpharays.medico.medico_utils.MedicoToast
import com.alpharays.medico.medico_utils.MedicoUtils
import com.alpharays.medico.medico_utils.MedicoUtils.Companion.ComposableNoNetworkFound
import com.alpharays.medico.medico_utils.MedicoUtils.Companion.getBalloonCount
import com.alpharays.medico.medico_utils.MedicoUtils.Companion.isInternetAvailable
import com.alpharays.medico.medico_utils.MedicoUtils.Companion.updateBalloonCount
import com.alpharays.medico.medico_utils.connectivity.ConnectivityObserver
import com.alpharays.medico.medico_utils.getMedicoViewModel
import com.alpharays.medico.presentation.common.CustomScaffold
import com.alpharays.medico.presentation.common.TopAppBar
import com.skydoves.balloon.compose.Balloon
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(navController: NavController, modifier: Modifier) {
    val homeScreenUseCase = MedicoApp
        .getInstance()
        .getMedicoInjector()
        .getHomeUseCase()
    val homeViewModel: HomeViewModel = getMedicoViewModel(homeScreenUseCase)
    val context = LocalContext.current
    val connectivityStatus = isInternetAvailable(context)
    LaunchedEffect(connectivityStatus) {
        homeViewModel.updateNetworkStatus(connectivityStatus)
    }

    Surface(modifier = modifier.fillMaxSize()) {
        CustomScaffold(navController = navController, isTopBarPresent = true, topBarContent =
        {
            TopAppBar(
                navController = navController
            )
        }) { innerPadding ->
            HomeAppointmentScreen(
                navController,
                homeViewModel,
                Modifier,
                innerPadding,
                connectivityStatus
            )
        }
    }
}


@Composable
fun HomeAppointmentScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    modifier: Modifier,
    paddingValues: PaddingValues,
    isInternetAvailable: ConnectivityObserver.Status,
) {
    val context = LocalContext.current
    var homeAppointments by remember {
        mutableStateOf<Array<Appointment?>?>(null)
    }

    var homeAppointmentList by remember {
        mutableStateOf<Array<Appointment?>?>(null)
    }

    val cachedAppointmentList by homeViewModel.combinedAppointmentListState.collectAsStateWithLifecycle()

    LaunchedEffect(cachedAppointmentList) {
        with(cachedAppointmentList) {
//            if (isLoading != null && isLoading == true) {
//                CustomToast.showToast(context, "Loading appointments")
//                return@LaunchedEffect
//            }

            if (!error.isNullOrEmpty()) {
                // error - response from the server
                MedicoToast.showToast(context, error.toString())
                return@LaunchedEffect
            }


            val curData = data?.data?.get(0)
            val appointments: ArrayList<Appointment> = ArrayList()

            curData?.let { appointmentListData ->
                appointmentListData.appointmentList?.forEach { appointment ->
                    appointments.add(appointment)
                    println(appointment)
                }
            }

            // filtered appointment list
            homeAppointments = appointments.filter {
                it.appointmentStatus == AppointmentType.Confirmed.name.uppercase()
            }.toTypedArray()

            // original appointment list
            homeAppointmentList = appointments.toTypedArray()
        }
    }


    Column(
        modifier
            .padding(paddingValues)
            .fillMaxWidth()
            .padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 4.dp)
    ) {
        ComposableAppointments(
            modifier,
            navController,
            context,
            homeAppointments,
            homeAppointmentList,
            isInternetAvailable,
            homeViewModel
        )
    }
}


@Composable
fun ComposableScheduleText() {
    Text(
        modifier = Modifier.padding(start = 5.dp),
        text = "Schedule",
        style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold)
    )
}

@Composable
fun ComposableAppointments(
    modifier: Modifier,
    navController: NavController,
    rowContext: Context,
    homeAppointments: Array<Appointment?>?,
    homeAppointmentList: Array<Appointment?>?,
    isInternetAvailable: ConnectivityObserver.Status,
    homeViewModel: HomeViewModel
) {
    var finalAppointmentList by remember {
        mutableStateOf<Array<Appointment?>?>(null)
    }

    LaunchedEffect(homeAppointments) {
        finalAppointmentList = homeAppointments
    }

    var buttonClicked by remember {
        mutableStateOf(AppointmentType.Confirmed.name)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val buttonModifier = Modifier
            .weight(1f)
            .padding(horizontal = 4.dp)

        AppointmentTypeButtonsComp(
            buttonClicked,
            AppointmentType.All,
            buttonModifier,
            homeAppointmentList
        ) { buttonType, newAppointments ->
            buttonClicked = buttonType
            finalAppointmentList = newAppointments
        }

        AppointmentTypeButtonsComp(
            buttonClicked,
            AppointmentType.Confirmed,
            buttonModifier,
            homeAppointmentList
        ) { buttonType, newAppointments ->
            buttonClicked = buttonType
            finalAppointmentList = newAppointments
        }

        AppointmentTypeButtonsComp(
            buttonClicked,
            AppointmentType.Completed,
            buttonModifier,
            homeAppointmentList
        ) { buttonType, newAppointments ->
            buttonClicked = buttonType
            finalAppointmentList = newAppointments
        }

        AppointmentTypeButtonsComp(
            buttonClicked,
            AppointmentType.Cancelled,
            buttonModifier,
            homeAppointmentList
        ) { buttonType, newAppointments ->
            buttonClicked = buttonType
            finalAppointmentList = newAppointments
        }

    }

    Box(modifier = Modifier.fillMaxSize()) {
        ComposableAppointmentsList(
            navController = navController,
            modifier = modifier,
            appointmentListData = finalAppointmentList,
            isInternetAvailable = isInternetAvailable,
            homeViewModel = homeViewModel
        )
        Icon(
            modifier = Modifier
                .size(52.dp)
                .align(Alignment.BottomEnd)
                .padding(0.dp, 0.dp, 3.dp, 0.5.dp)
                .background(Color(0xFFF7FFFE), RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .border(0.5.dp, Color.Black, RoundedCornerShape(12.dp))
                .clickable {
                    MedicoToast.showToast(rowContext, "Adding new appointment")
                    navController.navigate(AppScreens.AppointmentStartScreen.route)
                },
            imageVector = Icons.Default.Add,
            contentDescription = "Add new appointment",
            tint = Color.Blue
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppointmentTypeButtonsComp(
    buttonClicked: String,
    buttonType: AppointmentType,
    modifier: Modifier,
    homeAppointmentListState: Array<Appointment?>?,
    buttonClickedType: (String, Array<Appointment?>?) -> Unit,
) {
    val color1 = Color(0xFF3a4c5c) // selected color
    val color2 = Color(0xFFB4C0FF) // unselected color
    val color3 = Color(0xFFB5C8FF)
    val customButtonColors = ButtonDefaults.buttonColors(
        containerColor = if (buttonClicked == buttonType.name) color1 else color2,
    )
    val textColor = if (buttonClicked == buttonType.name) Color.White else Color.Black
    var homeAppointmentCopy by remember {
        mutableStateOf<Array<Appointment?>?>(emptyArray())
    }
    val upcoming = AppointmentType.Confirmed.name
    val completed = AppointmentType.Completed.name
    val cancelled = AppointmentType.Cancelled.name

    val interactionSource = remember { MutableInteractionSource() }
    val indication = LocalIndication.current

    OutlinedButton(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = customButtonColors,
        contentPadding = PaddingValues(0.dp),
        onClick = {
            when (buttonType) {
                AppointmentType.Confirmed -> {
                    homeAppointmentCopy = homeAppointmentListState
                        ?.filter {
                            it?.appointmentStatus == upcoming.uppercase()
                        }
                        ?.toTypedArray()
                    buttonClickedType(upcoming, homeAppointmentCopy)
                }

                AppointmentType.Completed -> {
                    homeAppointmentCopy = homeAppointmentListState
                        ?.filter {
                            it?.appointmentStatus == completed.uppercase()
                        }
                        ?.toTypedArray()
                    buttonClickedType(completed, homeAppointmentCopy)
                }

                AppointmentType.Cancelled -> {
                    homeAppointmentCopy = homeAppointmentListState
                        ?.filter {
                            it?.appointmentStatus == cancelled.uppercase()
                        }
                        ?.toTypedArray()
                    buttonClickedType(cancelled, homeAppointmentCopy)
                }

                AppointmentType.All -> {
                    homeAppointmentCopy = homeAppointmentListState
                    buttonClickedType(AppointmentType.All.name, homeAppointmentCopy)
                }
            }
        }
    ) {
        Text(
            modifier = Modifier.basicMarquee(Int.MAX_VALUE),
            text = buttonType.name,
            style = TextStyle(fontSize = 14.sp),
            textAlign = TextAlign.Center,
            color = textColor,
            maxLines = 1
//                .clickable(
//                    interactionSource = interactionSource,
//                    indication = indication
//                ){}
        )
    }
}


@Composable
fun ComposableAppointmentsList(
    navController: NavController,
    modifier: Modifier,
    appointmentListData: Array<Appointment?>?,
    isInternetAvailable: ConnectivityObserver.Status,
    homeViewModel: HomeViewModel
) {
    var noAppointments by remember { mutableStateOf(false) }
    var previousInternetStatus by remember {
        mutableStateOf(ConnectivityObserver.Status.Losing)
    }
    val context = LocalContext.current

    appointmentListData?.let { arr ->
        if (arr.isNotEmpty()) {
            // appointment list is guaranteed to be coming
            LazyColumn(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp)
            ) {
                items(arr) { appointmentList ->
                    appointmentList?.let {
                        AppointmentCard(navController, it, modifier)
                    }
                }
            }
        } else {
            LaunchedEffect(arr) {
                delay(2500L)
                noAppointments = true
            }
            if (noAppointments) {
                if (isInternetAvailable == ConnectivityObserver.Status.Unavailable) {
                    MedicoToast.showToast(context, NO_CONNECTION)
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 10.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = "No appointments found",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.W400,
                            fontFamily = FontFamily(Font(R.font.courier_prime_regular))
                        )
                    )
                    Icon(
                        modifier = Modifier
                            .size(140.dp)
                            .padding(top = 20.dp),
                        painter = painterResource(id = R.drawable.empty_list),
                        contentDescription = "No appointments found",
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }


    if (previousInternetStatus == ConnectivityObserver.Status.Lost && isInternetAvailable == ConnectivityObserver.Status.Available) {
        // earlier connection was lost and now connection is re-established : reload screen
        MedicoToast.showToast(context, "Connected - Reloading appointments")
        ComposableNoNetworkFound(context, modifier, homeViewModel, false)
    }

    LaunchedEffect(isInternetAvailable) {
        previousInternetStatus = isInternetAvailable
    }
}


@Composable
fun AppointmentCard(navController: NavController, appointment: Appointment, modifier: Modifier) {
    val context = LocalContext.current
    val color = MedicoUtils.getMedicoColor(context, R.color.bluish_gray)
    var itemHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current

    Card(
        modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, Color(color), RoundedCornerShape(10.dp))
            .onSizeChanged {
                itemHeight = with(density) { it.height.toDp() }
            }
            .clickable {
                navController.navigate(AppScreens.PatientScreen.route)
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier.padding(16.dp)) {
            ComposablePatientUpperRow(appointment)
            ComposablePatientAppointmentDate(appointment)
            ComposablePatientAppointmentTime(appointment)
            ComposablePatientBalloonRow(appointment, itemHeight)
        }
    }
}


@Composable
fun ComposablePatientUpperRow(appointment: Appointment) {
    Row {
        Column(Modifier.weight(0.8f)) {
            ComposablePatientName(appointment)
            ComposablePatientDisease(appointment)
        }

        ComposablePatientAvatar(
            Modifier
                .weight(0.2f)
                .align(Alignment.CenterVertically)
        )
    }
}


@Composable
fun ComposablePatientName(appointment: Appointment) {
    Text(
        text = appointment.patientName.toString(),
        modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp),
        style = TextStyle(fontSize = 18.sp, fontFamily = FontFamily.Monospace)
    )
}


@Composable
fun ComposablePatientDisease(appointment: Appointment) {
    Text(
        text = "Issue: ${appointment.patientDisease}",
        modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp),
        fontWeight = FontWeight.Bold
    )
}


@Composable
fun ComposablePatientAvatar(modifier: Modifier) {
    val painter = painterResource(id = R.drawable.male)
    Image(
        painter = painter,
        contentDescription = null,
        modifier.size(50.dp)
    )
}


@Composable
fun ComposablePatientAppointmentDate(appointment: Appointment) {
    val painter = rememberVectorPainter(image = Icons.Outlined.CalendarMonth)
    Row(
        Modifier.padding(2.dp, 15.dp, 5.dp, 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painter,
            contentDescription = "date of appointment",
            tint = Color(0xFF5A7271)
        )
        Text(
            text = "${appointment.date}",
            Modifier.padding(start = 10.dp)
        )
    }
}


@Composable
fun ComposablePatientAppointmentTime(appointment: Appointment) {
    val painter = rememberVectorPainter(image = Icons.Outlined.AccessTime)
    Row(
        Modifier
            .padding(5.dp, 5.dp, 5.dp, 15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painter,
            contentDescription = "time of appointment",
            tint = Color(0xFF5A7271)
        )
        Text(
            text = "${appointment.time}",
            Modifier.padding(start = 10.dp),
            fontWeight = FontWeight.W600
        )
    }
}


@Composable
fun ComposablePatientBalloonRow(appointment: Appointment, itemHeight: Dp) {
    val balloonStatus by remember {
        mutableStateOf(MedicoUtils.Companion.Balloon.isBalloonShown())
    }
    val balloonCount by remember {
        mutableIntStateOf(getBalloonCount().toInt())
    }

    if (!balloonStatus && balloonCount < 4) {
        MedicoUtils.Companion.Balloon.setBalloonStatus(true)
        updateBalloonCount()
        Balloon(
            builder = MedicoUtils.balloon(),
            balloonContent = {
                Text(
                    text = "Long press to change status",
                    textAlign = TextAlign.End,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        ) { balloonWindow ->
            ComposablePatientLastRow(appointment, itemHeight)
            balloonWindow.showAlignBottom(xOff = 20)
        }
    } else {
        ComposablePatientLastRow(appointment, itemHeight)
    }
}


@Composable
fun ComposablePatientLastRow(appointment: Appointment, itemHeight: Dp) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        ComposablePatientAppointmentStatus(Modifier, appointment, itemHeight)
    }
}


@Composable
fun ComposablePatientAppointmentStatus(
    modifier: Modifier,
    appointment: Appointment,
    itemHeight: Dp
) {
    val context = LocalContext.current
    val appointmentPainter = painterResource(id = R.drawable.baseline_circle_24)
    var pressOffset by remember { mutableStateOf(DpOffset.Zero) }
    val interactionSource = remember { MutableInteractionSource() }
    val indication = LocalIndication.current
    var isContextMenuVisible by remember { mutableStateOf(false) }
    val markAsOptions = listOf("Cancel", "Archive", "Postpone", "Old", "New")


    val infiniteTransition = rememberInfiniteTransition(label = "twinkling_icon")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 850,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ), label = "twinkling_icon"
    )


    val patientStatusPainter = when (appointment.oldNewStatus) {
        "new" -> {
            painterResource(id = R.drawable.new_patient)
        }

        "old" -> {
            painterResource(id = R.drawable.old_patient)
        }

        else -> {
            painterResource(id = R.drawable.new_patient)
        }
    }

    val (iconAlpha, iconTint) = when (appointment.appointmentStatus?.lowercase()) {
        AppointmentType.Confirmed.name.lowercase().replace("ed", "") -> {
            alpha to Color.Green
        }

        AppointmentType.Completed.name.lowercase() -> {
            1f to Color.Blue
        }

        AppointmentType.Cancelled.name.lowercase() -> {
            1f to Color.Red
        }

        else -> {
            1f to Color.Green
        }
    }


    Row(
        modifier
            .padding(horizontal = 5.dp, vertical = 5.dp)
            .background(Color(0xFFEFFFFD), RoundedCornerShape(6.dp))
            .indication(interactionSource, indication)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        isContextMenuVisible = true
                        pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                    }
                )
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Icon(
            tint = iconTint,
            painter = appointmentPainter,
            modifier = Modifier
                .padding(8.dp)
                .size(10.dp)
                .alpha(iconAlpha),
            contentDescription = "twinkling appointment status - confirmed/completed/cancelled"
        )
        Text(
            fontWeight = FontWeight.W600,
            text = "${appointment.appointmentStatus}",
            modifier = Modifier.padding(start = 5.dp, end = 5.dp)
        )


        Icon(
            modifier = Modifier
                .size(30.dp)
                .padding(end = 3.dp),
            painter = patientStatusPainter,
            contentDescription = "patient status - old or new",
            tint = Color.Unspecified
        )
    }


    DropdownMenu(
        modifier = modifier,
        expanded = isContextMenuVisible,
        onDismissRequest = {
            isContextMenuVisible = false
        },
        offset = pressOffset.copy(
            y = pressOffset.y - itemHeight + 30.dp
        )
    ) {
        markAsOptions.forEachIndexed { index, item ->
            DropdownMenuItem(
                text = { Text(text = item) },
                onClick = {
                    MedicoToast.showToast(
                        context, when (index) {
                            0 -> "Cancelled"
                            1 -> "Archived"
                            2 -> "Postponed"
                            else -> "Marked as $item"
                        }
                    )
                    isContextMenuVisible = false
                }
            )
        }
    }
}


enum class AppointmentType {
    Confirmed, Completed, Cancelled, All
}


//@PreviewScreenSizes
@Preview
@Composable
fun HomeScreenPreview() {
    val apt = Appointment("Shivang", "D", "Male", "", "", "", "COMPLETED")
//    ComposablePatientLastRow(apt, 10.dp)
}