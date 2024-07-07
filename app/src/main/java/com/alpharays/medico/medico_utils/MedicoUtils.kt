package com.alpharays.medico.medico_utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alpharays.medico.MainActivity
import com.alpharays.medico.MedicoApp
import com.alpharays.medico.R
import com.alpharays.medico.presentation.home_screen.HomeViewModel
import com.alpharays.medico.medico_utils.MedicoConstants.AUTH_TOKEN_KEY
import com.alpharays.medico.medico_utils.MedicoConstants.AUTH_TOKEN_SHARED_PREF
import com.alpharays.medico.medico_utils.MedicoConstants.BALLOON_STATUS_COUNT
import com.alpharays.medico.medico_utils.MedicoConstants.BALLOON_STATUS_COUNT_KEY
import com.alpharays.medico.medico_utils.MedicoConstants.MEDICO_DOC_ID
import com.alpharays.medico.medico_utils.MedicoConstants.MEDICO_DOC_ID_KEY
import com.alpharays.medico.medico_utils.MedicoConstants.NO_CONNECTION
import com.alpharays.medico.medico_utils.connectivity.ConnectivityObserver
import com.alpharays.medico.medico_utils.connectivity.NetworkConnectivityObserver
import com.alpharays.mymedicommfma.communityv2.community_app.community_utils.CommunityUtils
import com.alpharays.mymedicommfma.communityv2.community_app.presentation.community_screen.CommunityViewModel
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon.Builder
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonHighlightAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.rememberBalloonBuilder
import com.skydoves.balloon.overlay.BalloonOverlayRoundRect

class MedicoUtils {
    companion object {
        val context = MedicoApp.getInstance()
        fun setAuthToken(token: String) {
            val authTokenSharedPref = context.getSharedPreferences(AUTH_TOKEN_SHARED_PREF, MODE_PRIVATE)
            authTokenSharedPref.edit().putString(AUTH_TOKEN_KEY, token).apply()
        }

        fun getAuthToken(): String {
            val authTokenSharedPref = context.getSharedPreferences(AUTH_TOKEN_SHARED_PREF, MODE_PRIVATE)
            return authTokenSharedPref.getString(AUTH_TOKEN_KEY, null).toString()
        }

        fun signOut(){
            setAuthToken("")
            CommunityUtils.setAuthToken(context,"")
            MedicoToast.showToast(context, "Signing out...")
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }

        fun setDocId(docId: String) {
            val docIdSharedPref = context.getSharedPreferences(MEDICO_DOC_ID, MODE_PRIVATE)
            docIdSharedPref.edit().putString(MEDICO_DOC_ID_KEY, docId).apply()
        }

        fun getDocId(): String {
            val docId = "652777d825ffc25072a940b7"
            val docIdSharedPref = context.getSharedPreferences(MEDICO_DOC_ID, MODE_PRIVATE)
            return docIdSharedPref.getString(MEDICO_DOC_ID_KEY, docId).toString()
        }

        fun updateBalloonCount(){
            val balloonStatusSharedPref = context.getSharedPreferences(BALLOON_STATUS_COUNT, MODE_PRIVATE)
            val count = (getBalloonCount().toInt() + 1).toString()
            balloonStatusSharedPref.edit().putString(BALLOON_STATUS_COUNT_KEY, count).apply()
        }

        fun getBalloonCount(): String{
            val balloonStatusSharedPref = context.getSharedPreferences(BALLOON_STATUS_COUNT, MODE_PRIVATE)
            return balloonStatusSharedPref.getString(BALLOON_STATUS_COUNT_KEY, "0") ?: "0"
        }

        fun getMedicoColor(context: Context, color: Int): Int {
            return context.getColor(color)
        }

        object NetworkCheck{
            private var alreadyLost = false
            fun setNetworkStatus(status: Boolean){
                alreadyLost = status
            }
            fun isNetworkAlreadyLost(): Boolean{
                return alreadyLost
            }
        }

        object Balloon{
            private var shown = false
            fun isBalloonShown() = shown
            fun setBalloonStatus(status: Boolean){
                shown = status
            }
        }

        private lateinit var connectivityObserver: ConnectivityObserver
        @Composable
        fun isInternetAvailable(context: Context): ConnectivityObserver.Status {
            connectivityObserver = NetworkConnectivityObserver(context)
            val status by connectivityObserver.observe().collectAsState(
                initial = ConnectivityObserver.Status.Unavailable
            )
            return status
        }

        @Composable
        fun <T> ComposableNoNetworkFound(context: Context, modifier: Modifier, viewModel: T, toShow: Boolean = true) {
            var reLoadScreen by remember { mutableStateOf(false) }
            if(toShow){
                val painter = painterResource(id = R.drawable.no_internet)
                Column(
                    modifier = modifier.padding(top = 10.dp, start = 5.dp, end = 5.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painter,
                        contentDescription = "No internet found",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(200.dp)
                    )

                    Text(
                        modifier = modifier,
                        text = MedicoConstants.SOMETHING_WENT_WRONG,
                        style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.W600, textAlign = TextAlign.Center)
                    )

                    Text(
                        modifier = modifier,
                        text = MedicoConstants.NO_CONNECTION_MSG,
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.W300, textAlign = TextAlign.Center)
                    )

                    OutlinedButton(
                        shape = RoundedCornerShape(12.dp),
                        modifier = modifier,
                        onClick = {
                            reLoadScreen = true
                        }) {
                        Text(
                            text = "Refresh",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = Color.Blue
                        )
                    }
                }
            }

            if(reLoadScreen){
                if(isInternetAvailable(context) == ConnectivityObserver.Status.Unavailable){
                    MedicoToast.showToast(context, NO_CONNECTION)
                    reLoadScreen = false
                    return
                }
                ScreenReload(viewModel)
            }
        }

        // TODO: screenReload ? viewModel is in different modules now, will this function work ?
        @Composable
        fun <T> ScreenReload(viewModel: T) {
            LaunchedEffect(Unit){
                when(viewModel){
                    is HomeViewModel -> {
                        viewModel.retryGettingRemoteAppointments()
                    }

//                    is ProfileViewModel -> {
//                        viewModel.retryGettingProfileData()
//                    }

                    is CommunityViewModel -> {
                //        viewModel.retryGettingPosts()  check_kr_lena
                    }
                }
            }
        }

        @Composable
        fun balloon(): Builder {
            val builder = rememberBalloonBuilder {
                setArrowSize(10)
                setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                setArrowPosition(0.75f)
                setArrowOrientation(ArrowOrientation.TOP)
                setWidth(BalloonSizeSpec.WRAP)
                setHeight(BalloonSizeSpec.WRAP)
                setPadding(9)
                setCornerRadius(8f)
                setBackgroundColorResource(R.color.bluish_gray)
                setAutoDismissDuration(4000L)
                setBalloonAnimation(BalloonAnimation.ELASTIC)
                setIsVisibleOverlay(true)
                setOverlayColorResource(R.color.transparent)
                setOverlayPaddingResource(R.dimen.overlayPaddingResource)
                setBalloonHighlightAnimation(BalloonHighlightAnimation.BREATH)
                setOverlayShape(
                    BalloonOverlayRoundRect(
                        R.dimen.balloonOverlayRadius,
                        R.dimen.balloonOverlayRadius
                    )
                )
                setDismissWhenClicked(true)
            }
            return builder
        }
    }
}