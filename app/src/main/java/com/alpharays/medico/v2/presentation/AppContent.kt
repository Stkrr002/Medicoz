package com.alpharays.medico.v2.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alpharays.medico.R
import com.alpharays.medico.presentation.common.BottomBarNavItemIcon
import com.alpharays.medico.presentation.theme.MedicoTheme
import com.alpharays.medico.presentation.theme.backgroundWhite
import com.alpharays.medico.v2.navigation.AppNavGraph
import com.google.accompanist.insets.ProvideWindowInsets

@Composable
fun AppContent() {
    ProvideWindowInsets {
        MedicoTheme {
            val tabs = remember { BottomTabs.entries.toTypedArray() }
            val navController = rememberNavController()
            Scaffold(
                topBar = {
                    TopAppBar(navController)
                },
                containerColor = backgroundWhite,
                bottomBar = { BottomBar(navController = navController, tabs) }
            ) { innerPaddingModifier ->
                AppNavGraph(
                    navController = navController,
                    modifier = Modifier.padding(innerPaddingModifier)
                )
            }
        }
    }
}

@Composable
fun TopAppBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Medico", style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        )
        Image(
            modifier = Modifier
                .clickable {
                    navController.navigate("jarvis_screen")
                }
                .size(40.dp),
            painter = painterResource(id = R.drawable.ic_robot_doctor),
            contentDescription = "AI Consultant",
        )
    }

}


@Composable
fun BottomBar(navController: NavController, tabs: Array<BottomTabs>) {
    val routes = remember { BottomTabs.entries.map { it.route } }
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry.value?.destination?.route
    val newBottomNavColor = Color(0xFFF6F0FF)
    if (currentDestination in routes) {
        BottomAppBar(
            modifier = Modifier
                .padding(5.dp, 0.dp, 5.dp, 5.dp)
                .clip(shape = RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp))
                .background(newBottomNavColor),

            actions = {
                tabs.forEach { navItem ->
                    val validNavigation = currentDestination != navItem.route
                    val selected = navItem.route == currentDestination
                    val label = navItem.name

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (validNavigation) {
                                if (navItem.route == "home") {
                                    navController.popBackStack(
                                        navController.graph.startDestinationId,
                                        inclusive = true
                                    )
                                } else {
                                    navController.popBackStack(
                                        navController.graph.startDestinationId,
                                        inclusive = false
                                    )
                                }
                                navController.navigate(navItem.route)
                            }
                        },
                        icon = {
                            BottomBarNavItemIcon(navItem.icon, navItem.description)
                        },
                        label = {
                            Text(
                                text = label,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    )
                }
            },
            containerColor = newBottomNavColor
        )
    }
}
