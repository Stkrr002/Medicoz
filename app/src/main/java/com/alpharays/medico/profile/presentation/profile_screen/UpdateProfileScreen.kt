package com.alpharays.medico.profile.presentation.profile_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alpharays.medico.profile.presentation.navigation.AppScreens
import com.alpharays.medico.profile.profile_utils.util.MedicoToast

@Composable
fun UpdateProfileScreen(navController: NavController) {
    val color = Color(0xFFF5F6FF)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = color,
        topBar = {
            ComposableUpdateProfileTopBar(navController)
        },
        bottomBar = {
            ComposableUpdateProfileBottomBar(navController)
        }
    ) { paddingValues ->
        Surface(
            color = color,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            ComposableProfileUpdates()
        }
    }
}


@Composable
fun ComposableProfileUpdates() {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            ComposableUpdateProfileOutlinedTextField()
        }
        item {
            ComposableUpdateProfileOutlinedTextField()
        }
    }
}


@Composable
fun ComposableUpdateProfileOutlinedTextField() {
    val textColor0 = Color(0xFF006372)
    val textColor = Color(0xFF003D46)
    val headingLabel = "Add name"
    var profileInputField by remember { mutableStateOf("") }
    val color = MaterialTheme.colorScheme.onPrimary
    val style = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        color = textColor
    )

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        value = profileInputField,
        onValueChange = { newText ->
            profileInputField = newText
        },
        label = {
            Text(text = headingLabel, style = style)
        },
        textStyle = TextStyle(fontSize = 14.sp, color = color),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.LightGray,
        )
    )
}


@Composable
fun ComposableUpdateProfileTopBar(navController: NavController) {
//    val color = Color(0xFF71FFEC) : CAN_BE_USED
//    val ripple = rememberRipple(bounded = false, radius = 24.dp, color = color) : CAN_BE_USED
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(0.dp, 0.dp, 5.dp, 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .padding(8.dp)
                    .size(20.dp)
                    .clickable {
                        navController.navigate("profile") {  // TODO: changed from [AppScreens(PROFILE_SCREEN_ROUTE)] -> "profile"
                            popUpTo(AppScreens.UpdateProfileScreen.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                imageVector = Icons.Default.Close,
                contentDescription = "close updating profile"
            )
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
                text = "Update profile",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            )
        }
    }
}


@Composable
fun ComposableUpdateProfileBottomBar(navController: NavController) {
    val interactionSource = remember { MutableInteractionSource() }
    val color = Color(0xFF768BFF)
    val context = LocalContext.current
    val ripple = rememberRipple(bounded = false, radius = 24.dp, color = color)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp, 18.dp, 0.dp, 0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = ripple,
                    ) {
                        MedicoToast.showToast(context, "Saved")
                        navController.navigate(AppScreens.ProfileScreen.route) {
                            popUpTo(AppScreens.UpdateProfileScreen.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                    .padding(8.dp)
            ) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = "Save",
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
                    color = Color.Blue
                )
            }
        }
    }
}


//@PreviewScreenSizes
@Preview
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()
    UpdateProfileScreen(navController = navController)
}