package com.alpharays.medico.presentation.auth_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.alpharays.medico.R
import com.alpharays.medico.presentation.navigation.AppScreens
import com.alpharays.medico.medico_utils.MedicoConstants.NO_CONNECTION
import com.alpharays.medico.medico_utils.MedicoToast
import com.alpharays.medico.medico_utils.MedicoUtils
import com.alpharays.medico.medico_utils.connectivity.ConnectivityObserver
import com.alpharays.mymedicommfma.communityv2.community_app.community_utils.CommunityUtils

@Composable
fun AuthScreen(authViewModel: AuthViewModel, navController: NavController) {
    val context = LocalContext.current
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.medico_entry))
    val connectivityStatus = MedicoUtils.isInternetAvailable(context)
    LaunchedEffect(connectivityStatus) {
        authViewModel.updateNetworkStatus(connectivityStatus)
    }

    val styleA = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = FontFamily.Monospace
    )
    val styleB = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = FontFamily.Monospace
    )
    val styleC = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Light
    )

    LaunchedEffect(Unit){
        MedicoToast.showToast(context, "Welcome")
    }


    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
                .padding(16.dp)
        ) {
            LottieAnimation(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f),
                composition = composition,
                iterations = LottieConstants.IterateForever,
                speed = 1.5f,
                contentScale = ContentScale.Fit
            )

            AuthScreenTextFields("Welcome to Medico", textStyle = styleA)

            AuthScreenTextFields(
                "Enter your mobile number",
                Modifier.padding(top = 20.dp),
                textStyle = styleB
            )

            AuthScreenTextFields(
                "We'll send you a verification code.",
                Modifier.padding(top = 20.dp),
                textStyle = styleC
            )

            UserInputComp(authViewModel, navController)
        }
    }
}


@Composable
fun AuthScreenTextFields(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(),
) {
    Text(text = text, style = textStyle, modifier = modifier)
}


@Composable
fun UserInputComp(authViewModel: AuthViewModel, navController: NavController) {
    var userNumber by remember {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val networkStatus by authViewModel.networkStatus.collectAsState()
    var isInternetAvailable by remember {
        mutableStateOf(false)
    }

    BindObserver(authViewModel, navController)

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        value = userNumber,
        onValueChange = { newText ->
            if (newText.length <= 10) {
                userNumber = newText
                if (userNumber.length == 10) keyboardController?.hide()
            }
        },
        label = {
            Text(text = "Enter your number")
        },
        singleLine = true,
        maxLines = 1,
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = Color(0xFF00897B)
        ),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
    )


    Text(
        text = "${userNumber.length} / 10",
        textAlign = TextAlign.End,
        style = TextStyle(fontSize = 10.sp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 12.dp)
    )

    LaunchedEffect(networkStatus){
        val status = authViewModel.networkStatus
        isInternetAvailable = status.value == ConnectivityObserver.Status.Available
    }

    OutlinedButton(
        onClick = {
            if (userNumber.length < 10) {
                MedicoToast.showToast(context, "Enter valid number")
                return@OutlinedButton
            }
            if(!isInternetAvailable){
                MedicoToast.showToast(context, NO_CONNECTION)
                return@OutlinedButton
            }
            authViewModel.getAuthTokenState(userNumber)
        }) {
        Text(
            text = "Continue",
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace
            )
        )
    }
}


@Composable
fun BindObserver(authViewModel: AuthViewModel, navController: NavController) {
    val authInfoState by authViewModel.authInfoStateFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(key1 = authInfoState) {
        with(authInfoState) {
            // loading auth state
            if (isLoading != null && isLoading == true) {
                MedicoToast.showToast(context, "Getting token")
                    println("loading - getting token")
                return@LaunchedEffect
            }

            // error auth state
            if (isError != null && isError?.isNotEmpty() == true) {
                MedicoToast.showToast(context, isError.toString())
                println("error : $isError")
                return@LaunchedEffect
            }

            // success auth state
            isSuccess?.let { userToken ->
                val token = userToken.token
                if(token.isNullOrEmpty()){
                    MedicoToast.showToast(context, "Authentication Error. Please try again later")
                    return@let
                }
                println("token : $token")

                // Save token to SharedPreferences
                CommunityUtils.setAuthToken(context,token)
                MedicoUtils.setAuthToken(token)
                navController.popBackStack()
                navController.navigate(AppScreens.HomeScreen.route)
            }
        }
    }
}


@Preview
@Composable
fun AuthPreview() {

}