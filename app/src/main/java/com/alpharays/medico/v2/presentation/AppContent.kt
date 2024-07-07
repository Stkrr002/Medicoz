package com.alpharays.medico.v2.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
fun BottomBar(navController: NavController, tabs: Array<BottomTabs>) {


    val routes = remember { BottomTabs.values().map { it.route } }


    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry.value?.destination?.route
//    val oldBottomNavColor = Color(0xFFB5C8FF)
    val newBottomNavColor = Color(0xFFF6F0FF)







    if (currentDestination in routes) {

        /*
        BottomNavigation(
            Modifier.navigationBarsHeight(additional = 56.dp)
        ) {
            tabs.forEach { tab ->
                BottomNavigationItem(
                    icon = { Icon(painterResource(tab.icon), contentDescription = null) },
                    label = { Text(stringResource(tab.title).uppercase(Locale.getDefault())) },
                    selected = currentRoute == tab.route,
                    onClick = {
                        if (tab.route != currentRoute) {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    alwaysShowLabel = false,
                    selectedContentColor = MaterialTheme.colors.secondary,
                    unselectedContentColor = LocalContentColor.current,
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        }

         */


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
