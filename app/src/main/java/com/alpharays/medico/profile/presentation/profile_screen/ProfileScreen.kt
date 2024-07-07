package com.alpharays.medico.profile.presentation.profile_screen

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.alpharays.medico.MedicoApp
import com.alpharays.medico.R
import com.alpharays.medico.profile.domain.model.profilescreen.Profile
import com.alpharays.medico.profile.domain.model.profilescreen.userposts.UserPosts
import com.alpharays.medico.profile.presentation.common.CustomScaffold
import com.alpharays.medico.profile.presentation.navigation.AppScreens
import com.alpharays.medico.profile.profile_utils.connectivity.ConnectivityObserver
import com.alpharays.medico.profile.profile_utils.util.MedicoToast
import com.alpharays.medico.profile.profile_utils.util.MedicoUtils
import com.alpharays.medico.profile.profile_utils.util.getMedicoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navController: NavController) {
    val profileScreenUseCase = MedicoApp
        .getInstance()
        .getProfileInjector()
        .getProfileUseCase()
    val profileViewModel: ProfileViewModel = getMedicoViewModel(profileScreenUseCase)

    val context = LocalContext.current
    val connectivityStatus = MedicoUtils.isInternetAvailable(context)
    var profileInfoResponse by remember {
        mutableStateOf(Profile())
    }
    LaunchedEffect(connectivityStatus) {
        profileViewModel.updateNetworkStatus(connectivityStatus)
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        val currentUser = true
        CustomScaffold(
            navController = navController,
            isTopBarPresent = true,
            topBarContent = {
                ComposableProfileTopBar(
                    currentUser = true,
                    context = context,
                    topBarModifier = Modifier.padding(top = 8.dp, start = 10.dp, end = 10.dp),
                    dropdownMenuModifier = Modifier,
                    navController = navController,
                    profileInfoResponse = profileInfoResponse
                )
            }
        ) { innerPadding ->
            ComposableProfileScreen(
                context,
                currentUser,
                Modifier,
                innerPadding,
                profileViewModel
            ){ profileInfo ->
                profileInfoResponse = profileInfo
            }
        }
    }
}


@Composable
fun ComposableProfileScreen(
    context: Context,
    currentUser: Boolean,
    modifier: Modifier,
    paddingValues: PaddingValues,
    profileViewModel: ProfileViewModel,
    profileInfo: (Profile) -> Unit
) {
    Column(
        modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(12.dp)) {
        ComposableProfileAbout(context, profileViewModel){ profileInfoResponse ->
            profileInfo(profileInfoResponse)
        }
        ComposableProfilePosts(context, profileViewModel)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposableProfileTopBar(
    currentUser: Boolean,
    context: Context,
    topBarModifier: Modifier,
    dropdownMenuModifier: Modifier,
    navController: NavController,
    profileInfoResponse : Profile
) {
    var isMoreOptionsClicked by remember { mutableStateOf(false) }

    if (currentUser) {
        Row(
            modifier = topBarModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = topBarModifier.weight(1f),
                text = "Profile",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
            )

            Spacer(modifier = topBarModifier.weight(1f))

            IconButton(onClick = {
                isMoreOptionsClicked = true
            }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More"
                )
            }

            if(isMoreOptionsClicked){
                ComposableProfileMoreOptions(
                    onDismiss = {
                        isMoreOptionsClicked = false
                    },
                    modifier = dropdownMenuModifier,
                    navController = navController,
                    profileInfoResponse = profileInfoResponse
                )
            }
        }
    }
    else {
        val painter = rememberVectorPainter(image = Icons.Default.KeyboardArrowLeft)
        var profileNameText by remember {
            mutableStateOf("")
        }
        var searchBarActive by remember {
            mutableStateOf(false)
        }
        // TODO: history items will be stored in ROOM DB, no need to store remotely
        val historyItems = remember {
            mutableStateListOf(
                "Shivang Gautam",
                "Sumit Kumar",
                "Vaibhav Soni",
                "Raman"
            ) // TODO : static data
        }

        Row(
            modifier = topBarModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchBar(
                modifier = topBarModifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                query = profileNameText,
                onQueryChange = {
                    profileNameText = it
                },
                onSearch = {
                    historyItems.add(profileNameText)
                    searchBarActive = false
                },
                active = searchBarActive,
                onActiveChange = {
                    searchBarActive = it
                },
                placeholder = {
                    Text(text = "Search")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                trailingIcon = {
                    Icon(
                        modifier = topBarModifier.clickable {
                            if (profileNameText.isNotEmpty()) profileNameText = ""
                            else searchBarActive = false
                        },
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Icon"
                    )
                }
            ) {
                LazyColumn{
                    items(historyItems){
                        Row(
                            modifier = topBarModifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                modifier = topBarModifier.padding(10.dp),
                                imageVector = Icons.Default.History,
                                contentDescription = "History Icon"
                            )
                            Text(text = it)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ComposableProfileMoreOptions(
    onDismiss: () -> Unit,
    modifier: Modifier,
    navController: NavController,
    profileInfoResponse: Profile
) {
    var profileData by remember { mutableStateOf(Profile()) }

    LaunchedEffect(profileInfoResponse){
        profileData = profileInfoResponse
    }

    val dropDownItems = listOf("Update profile", "Share profile via message", "Share profile", "Sign Out")
    var pressOffset by remember { mutableStateOf(DpOffset(0.dp, 0.dp)) }
    var itemClicked by remember { mutableStateOf("") }
    val interactionSource = remember{ MutableInteractionSource() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val dropDownBoxColor = Color(0xFFC7D5EC)
    val anotherDropDownBoxColor = Color(0xFFBBD6D5)

    Box(
        modifier = Modifier
            .indication(interactionSource, LocalIndication.current)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    pressOffset = DpOffset(offset.x.toDp(), offset.y.toDp())
                }
            },
        contentAlignment = Alignment.Center
    ){
        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp)),
        ){
            DropdownMenu(
                modifier = Modifier
                    .background(dropDownBoxColor)
                    .padding(end = 10.dp),
                expanded = true,
                onDismissRequest = { onDismiss() },
                offset = pressOffset.copy(y = pressOffset.y + 20.dp)
            ) {
                dropDownItems.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            coroutineScope.launch {
                                delay(500L)
                                itemClicked = item
                                MedicoToast.showToast(context, "Clicked: $item")
                                onDismiss()
                            }
                            handleDropDownItemClick(context, index, onDismiss, navController, profileData)
                        }
                    )
                }
            }
        }
    }
}


private fun handleDropDownItemClick(
    context: Context,
    index: Int,
    onDismiss: () -> Unit,
    navController: NavController,
    profileData: Profile
){
    onDismiss()
    when(index){
        0 -> {
            // navigate to update profile screen
            navController.navigate(AppScreens.UpdateProfileScreen.route)
        }

        1 -> {
            // TODO -> navigate to a SCREEN which shows the list of recipients whom the message/profile is to be shared
            MedicoToast.showToast(context, "Coming soon...")
        }

        2 -> {
            val title = "Sharing profile..."
            val textToShare = "Have a look at this profile of ${profileData.name} with ${profileData.yearsOfExp} of experience"
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, textToShare)
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(shareIntent, title))
            // TODO -> GPT - COPY PASTE -> how to use deep link in jetpack compose ? like in my app i want to share profile link and user clicks on it and the link opens my app
        }

        3 -> {
            // TODO -> remove data from room Db - if any other user logs-in, it may show cached result
            MedicoUtils.signOut()
        }
    }
}


@Composable
fun ComposableProfileAbout(context: Context, profileViewModel: ProfileViewModel, profileInfo: (Profile) -> Unit) {
    val painter = painterResource(id = R.drawable.doctor_profile)
    val color = MedicoUtils.getMedicoColor(context, R.color.bluish_gray)

    Column {
        Box(modifier = Modifier.padding(top = 10.dp, start = 8.dp, bottom = 6.dp)) {
            Image(
                modifier = Modifier
                    .border(1.dp, Color(color), RoundedCornerShape(60.dp))
                    .size(140.dp)
                    .clip(RoundedCornerShape(60.dp)),
                painter = painter,
                contentDescription = "Doctor profile"
            )
            IconButton(
                modifier = Modifier.padding(2.dp).align(Alignment.BottomEnd),
                onClick = {
                    MedicoToast.showToast(context, "Adding new photo")
                }) {
                Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = "Add photo")
            }
        }

        ComposableProfileAboutInfo(context, profileViewModel){ profile ->
            profileInfo(profile)
        }
    }
}


@Composable
fun ComposableProfileAboutInfo(context: Context, profileViewModel: ProfileViewModel, profileResponse: (Profile) -> Unit) {
    var profileName by remember {
        mutableStateOf<String?>(null)
    }
    var profileDepartment by remember {
        mutableStateOf<String?>(null)
    }
    var profileAge by remember {
        mutableStateOf<String?>(null)
    }
    var profileGender by remember {
        mutableStateOf<String?>(null)
    }
    var profileYoe by remember {
        mutableStateOf<String?>(null)
    }
    var profileQualifications by remember {
        mutableStateOf<String?>(null)
    }
    var profilePhoneNumber by remember {
        mutableStateOf<String?>(null)
    }
    var profileEmail by remember {
        mutableStateOf<String?>(null)
    }
    var profileAvatarImageUrl by remember {
        mutableStateOf<String?>(null)
    }

    val profileInfoResponse by profileViewModel.combinedProfileInfoData.collectAsStateWithLifecycle()

    LaunchedEffect(profileInfoResponse) {
        with(profileInfoResponse){
//            if(isLoading!=null && isLoading==true){
//                CustomToast.showToast(context, "Loading profile")
//                return@LaunchedEffect
//            }
//
//            if(!error.isNullOrEmpty()){
//                CustomToast.showToast(context, "Could not load profile")
//                return@LaunchedEffect
//            }

            profileInfo?.let { profile ->
                profileResponse(profile)
                profileName = profile.name
                profileDepartment = profile.department
                profileAge = profile.age
                profileGender = profile.gender
                profileYoe = profile.yearsOfExp
                profileQualifications = profile.qualifications
                profilePhoneNumber = profile.phone
                profileEmail = profile.email
                profileAvatarImageUrl = profile.avatarImageUrl
            }
        }
    }

    Text(
        modifier = Modifier.padding(top = 5.dp),
        text = profileName ?: "",
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    )

    Text(
        modifier = Modifier.padding(top = 10.dp),
        text = "${profileDepartment ?: ""} : ${profileQualifications ?: ""}",
        style = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.W600
        )
    )

    Text(
        modifier = Modifier.padding(top = 5.dp),
        text = "$profileYoe years of experience in $profileQualifications\nDelhi, India",
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Light
        )
    )

    Text(
        modifier = Modifier.padding(top = 10.dp),
        text = "500+ Connections",
        style = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Light
        )
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        OutlinedButton(
            shape = RoundedCornerShape(10.dp),
            onClick = {
                MedicoToast.showToast(context, "Sending request")
            }) {
            Text(text = "Send request")
        }
    }
}


@Composable
fun ComposableProfilePosts(context: Context, profileViewModel: ProfileViewModel) {
    val scope = rememberCoroutineScope()
    val profilePostsResponse by profileViewModel.combinedProfilePostsData.collectAsStateWithLifecycle()
    var currentProfilePosts by remember {
        mutableStateOf<List<UserPosts>?>(null)
    }
    val networkStatus by profileViewModel.networkStatus.collectAsStateWithLifecycle()
    var previousInternetStatus by remember {
        mutableStateOf(ConnectivityObserver.Status.Losing)
    }
    var isInternetAvailable by remember {
        mutableStateOf(false)
    }
    val lost = ConnectivityObserver.Status.Lost
    val unAvailable = ConnectivityObserver.Status.Unavailable

    if((previousInternetStatus == lost || previousInternetStatus == unAvailable) && networkStatus == ConnectivityObserver.Status.Available){
        // earlier connection was lost and now connection is re-established : reload screen
//        CustomToast.showToast(context, "Reloading") // TODO : why is it showing when navigated from upd profile screen to profile screen
//        MedicoUtils.ComposableNoNetworkFound(context, Modifier, profileViewModel, false)
    }

    LaunchedEffect(networkStatus){
        previousInternetStatus = networkStatus
        isInternetAvailable = networkStatus == ConnectivityObserver.Status.Available
    }

    LaunchedEffect(profilePostsResponse) {
        with(profilePostsResponse){
//            if (isLoading!=null && isLoading == true) {
//                CustomToast.showToast(context, "Loading your posts")
//                return@LaunchedEffect
//            }

//            if (!error.isNullOrEmpty()) {
//                CustomToast.showToast(context, "Could not load posts")
//                return@LaunchedEffect
//            }

            currentProfilePosts = profilePosts?.myPostsData
        }
    }

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        text = if (currentProfilePosts.isNullOrEmpty()) "No Activity" else "Activity",
        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
    )

    currentProfilePosts?.let {
        val painter = painterResource(id = R.drawable.celebrate_in_options)
        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 5.dp)) {
            items(it) { posts ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 3.dp, bottom = 9.dp),
                    colors = CardDefaults.cardColors(Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                    shape = RoundedCornerShape(6.dp)
                ){
                    Column {
                        Text(
                            modifier = Modifier.padding(4.dp),
                            text = "${posts.posterName} posted this - 1d",
                            fontSize = 16.sp
                        )

                        Row(verticalAlignment = Alignment.Top) {
                            Image(
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(4.dp),
                                painter = painter,
                                contentDescription = "Post image"
                            )

                            Text(
                                modifier = Modifier.padding(horizontal = 4.dp),
                                text = posts.postContent ?: "NA",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.W300
                                )
                            )
                        }


                        Text(
                            modifier = Modifier.padding(4.dp),
                            text = "0 Likes, 0 Comments",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.W400
                            )
                        )
                    }
                }
            }
        }
    }

    if(isInternetAvailable){
        if(currentProfilePosts.isNullOrEmpty()){
            LaunchedEffect(Unit) {
                scope.launch {
                    delay(10000L)
                    MedicoToast.showToast(context, "No posts found")
                }
            }
        }
    }
}
