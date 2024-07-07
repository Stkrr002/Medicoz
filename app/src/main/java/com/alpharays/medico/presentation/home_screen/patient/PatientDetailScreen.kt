package com.alpharays.medico.presentation.home_screen.patient


import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.alpharays.medico.R
import com.alpharays.medico.presentation.overlay_screen.OverlayService
import com.alpharays.medico.presentation.navigation.AppScreens
import com.alpharays.medico.medico_utils.MedicoToast
import org.jitsi.meet.sdk.BroadcastEvent
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import timber.log.Timber
import java.net.URL

@Composable
fun PatientDetailScreen(navController: NavController) {
    val context = LocalContext.current
    registerForBroadcastMessages(context)
    Surface(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        ComposablePatientScreen(context, navController)
    }
}


private val broadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        onBroadcastReceived(context, intent)
    }
}


private fun registerForBroadcastMessages(context: Context) {
    val intentFilter = IntentFilter()

    /* This registers for every possible event sent from JitsiMeetSDK
       If only some of the events are needed, the for loop can be replaced
       with individual statements:
       ex:  intentFilter.addAction(BroadcastEvent.Type.AUDIO_MUTED_CHANGED.action);
            intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.action);
            ... other events
     */
    for (type in BroadcastEvent.Type.entries) {
        intentFilter.addAction(type.action)
    }

    LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, intentFilter)
}


private fun onBroadcastReceived(context: Context?, intent: Intent?) {
    val service = Intent(context, OverlayService::class.java)
    if (intent != null) {
        val event = BroadcastEvent(intent)
        when (event.type) {
            BroadcastEvent.Type.CONFERENCE_JOINED -> {
                Timber.i(
                    "medico__Conference Joined with url%s",
                    event.data["url"]
                )
                context?.let {
                    val isServiceRunning = isOverlayServiceRunning(context)
                    if (!isServiceRunning) {
                        it.startService(service)
                    }
                }
            }


            BroadcastEvent.Type.CONFERENCE_BLURRED -> {
                Timber.e("my_CONFERENCE_BLURRED_blur")
            }


            BroadcastEvent.Type.CONFERENCE_TERMINATED -> {
                Timber.e("my_CONFERENCE_TERMINATED:medico__")
                context?.let {
                    val isServiceRunning = isOverlayServiceRunning(context)
                    if (isServiceRunning) {
                        it.stopService(service)
                    }
                }
            }

            else -> Timber.i("Received event: %s", event.type)
        }
    }
}


@Suppress("DEPRECATION")
private fun isOverlayServiceRunning(context: Context): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val services = manager.getRunningServices(Int.MAX_VALUE)
    val serviceName = OverlayService::class.java.name

    for (service in services) {
        if (serviceName == service.service.className) {
            return true
        }
    }
    return false
}


@Composable
fun ComposablePatientScreen(context: Context, navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        ComposablePatientUpperRow(context, navController)
        ComposablePatientCard(context)
    }
}


@Composable
fun ComposablePatientUpperRow(context: Context, navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
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
            text = "Patient Profile",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.W500
            )
        )
    }
}


@Composable
fun ComposablePatientCard(context: Context) {
    val painter = painterResource(id = R.drawable.male)
    val customButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFF5C6BC0),
        contentColor = Color.White,
    )
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.meet_loading_lottie))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .border(1.dp, Color(0xFF3B4557), RoundedCornerShape(5.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFC9D8F3)),
        shape = RoundedCornerShape(5.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.padding(5.dp)
            ) {
                Column(modifier = Modifier.weight(1f).padding(5.dp)) {
                    ComposablePatientRowDetails("Patient name", "Shivang Gautam")
                    ComposablePatientRowDetails("Disease", "Diabetes")
                    ComposablePatientRowDetails("Status", "Confirmed")
                    ComposablePatientRowDetails("Mark as", "Completed")
                }

                Image(
                    modifier = Modifier.padding(16.dp, 10.dp, 16.dp, 0.dp).size(50.dp),
                    painter = painter,
                    contentDescription = "Patient avatar"
                )
            }

            ComposableCustomMeetingButton(customButtonColors, composition)

            ComposablePatientHistoryCard(context, Modifier)
        }
    }
}


@Composable
fun ComposablePatientRowDetails(patientAttribute: String, patientAttributeValue: String) {
    Row(
        modifier = Modifier.padding(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(5.dp),
            text = "$patientAttribute: ",
            style = TextStyle(
                fontSize = 16.sp
            )
        )
        Text(
            modifier = Modifier.padding(5.dp),
            text = patientAttributeValue,
            style = TextStyle(
                fontSize = 16.sp
            )
        )
    }
}


@Composable
fun ComposableCustomMeetingButton(
    customButtonColors: ButtonColors,
    composition: LottieComposition?
) {
    var showStartMeetingButton by remember {
        mutableStateOf(true)
    }
    val context = LocalContext.current
    if (showStartMeetingButton) {
        OutlinedButton(
            border = BorderStroke(0.dp, Color.White),
            colors = customButtonColors,
            shape = RoundedCornerShape(10.dp),
            onClick = {
                showStartMeetingButton = false
            }) {
            Text(
                text = "Start Meeting",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = FontFamily.Monospace
                )
            )
        }
    } else {
        LottieAnimation(
            modifier = Modifier.height(60.dp).width(100.dp),
            composition = composition,
            iterations = LottieConstants.IterateForever,
            speed = 1f,
            contentScale = ContentScale.Crop
        )
        if (!Settings.canDrawOverlays(context)) {
            requestOverlayPermission(context)
            showStartMeetingButton = true
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                videoConferenceLoading(context)
                showStartMeetingButton = true
            }, 2000L)
        }
    }
}


private fun requestOverlayPermission(context: Context) {
    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
    intent.data = Uri.parse("package:" + context.packageName)
    Toast.makeText(context, "Allow overlay permission", Toast.LENGTH_SHORT).show()
    context.startActivity(intent)
}


private fun videoConferenceLoading(context: Context) {
    val roomId = "9968"
    val serverURL = URL("https://meet.jit.si/$roomId")
    val defaultOptions = JitsiMeetConferenceOptions.Builder()
        .setServerURL(serverURL) // When using JaaS, set the obtained JWT here
        //.setToken("MyJWT")
        // Different features flags can be set
        // .setFeatureFlag("toolbox.enabled", false)
        // .setFeatureFlag("filmstrip.enabled", false)
        .setFeatureFlag("pip.enabled", true)
        .setFeatureFlag("welcomepage.enabled", false)
        .setFeatureFlag("live-streaming.enabled", true)
        .setFeatureFlag("invite.enabled", false)
        .setFeatureFlag("video-share.enabled", false)
        .setFeatureFlag("fullscreen.enabled", true)
        .setAudioMuted(false)
        .setVideoMuted(false)
        .build()
    JitsiMeet.setDefaultConferenceOptions(defaultOptions)

    // Build options object for joining the conference. The SDK will merge the default one we set earlier and this one when joining.
    val options = JitsiMeetConferenceOptions.Builder()
        .setRoom(roomId) // Settings for audio and video
        .setAudioMuted(true)
        .setVideoMuted(true)
        .build()

    // Launch the new activity with the given options. The launch() method takes care of creating the required Intent and passing the options.
    JitsiMeetActivity.launch(context, options)
}


@Composable
fun ComposablePatientHistoryCard(context: Context, modifier: Modifier) {
    Card(
        modifier.fillMaxSize().padding(top = 15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(30.dp, 30.dp, 0.dp, 0.dp)
    ) {
        Column(
            modifier.padding(16.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Patient History",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.W600
                )
            )

            Text(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                text = "Last visits & Prescription",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500,
                    textAlign = TextAlign.End
                )
            )

            val historyItems = ArrayList<String>()
            historyItems.add("1 Aug, 2023")
            historyItems.add("20 Aug, 2023")
            historyItems.add("30 Sep, 2023")
            historyItems.add("1 Aug, 2023")
            historyItems.add("1 Aug, 2023")
            historyItems.add("20 Aug, 2023")
            historyItems.add("30 Sep, 2023")
            historyItems.add("1 Aug, 2023")

            LazyColumn(modifier.padding(top = 10.dp)) {
                items(historyItems) { historyItem ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.padding(5.dp).fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier.padding(top = 5.dp),
                            text = historyItem,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W400,
                                textAlign = TextAlign.End
                            )
                        )

                        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.edit_prescription_lottie))
                        LottieAnimation(
                            modifier = Modifier
                                .size(40.dp)
                                .padding(start = 5.dp)
                                .clickable {
                                    MedicoToast.showToast(
                                        context,
                                        "Showing prescription for $historyItem"
                                    )
                                },
                            composition = composition,
                            iterations = LottieConstants.IterateForever,
                            speed = 0.8f,
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PatientScreenPreview() {
    val context = LocalContext.current
    ComposablePatientScreen(context, rememberNavController())
}