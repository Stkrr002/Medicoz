package com.alpharays.medico.profile.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.alpharays.medico.R
import com.alpharays.medico.profile.presentation.navigation.AppScreens
import com.alpharays.mymedicommfma.communityv2.community_app.presentation.navigation.CommunityAppScreens

@Composable
fun CustomBottomNavigationBar(navController: NavController) {
    val home = "Home"
    val homeRoute = AppScreens.HomeScreen.route
    val homeIcon = R.drawable.home_icon
    val homeIconDesc = "Home screen"

    val profile = "Profile"
    val profileRoute = AppScreens.ProfileScreen.route
    val profileIcon = R.drawable.user
    val profileIconDesc = "Profile screen"

    val community = "Community"
    val communityRoute = CommunityAppScreens.CommunityScreen.route
    val communityIcon = R.drawable.community
    val communityIconDesc = "Community screen"

    val bottomNavigationItems = mutableListOf(
        BottomNavigationItem(
            name = home,
            route = homeRoute,
            icon = homeIcon,
            description = homeIconDesc
        ),
        BottomNavigationItem(
            name = profile,
            route = profileRoute,
            icon = profileIcon,
            description = profileIconDesc
        ),
        BottomNavigationItem(
            name = community,
            route = communityRoute,
            icon = communityIcon,
            description = communityIconDesc
        )
    )

    BottomAppBarComposable(navController, bottomNavigationItems)
}


@Composable
fun BottomAppBarComposable(
    navController: NavController,
    bottomNavigationItems: MutableList<BottomNavigationItem>
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry.value?.destination?.route
//    val oldBottomNavColor = Color(0xFFB5C8FF)
    val newBottomNavColor = Color(0xFFF6F0FF)

    BottomAppBar(
        modifier = Modifier
            .padding(5.dp, 0.dp, 5.dp, 5.dp)
            .clip(shape = RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp))
            .background(newBottomNavColor),

        actions = {
            bottomNavigationItems.forEach { navItem ->
                val validNavigation = currentDestination != navItem.route
                val selected = navItem.route == currentDestination
                val label = navItem.name.toString()

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (validNavigation) {
                            if (navItem.route == AppScreens.HomeScreen.route) {
                                navController.popBackStack(navController.graph.startDestinationId, inclusive = true)
                            }
                            else {
                                navController.popBackStack(navController.graph.startDestinationId, inclusive = false)
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


@Composable
fun BottomBarNavItemIcon(icon: Int?, description: String?) {
    icon?.let {
        val painter = painterResource(id = icon)
        Icon(
            painter = painter,
            contentDescription = description,
            modifier = Modifier.size(24.dp)
        )
    }
}


data class BottomNavigationItem(
    val name: String? = null,
    val route: String = "",
    val icon: Int? = null,
    val description: String? = null
)