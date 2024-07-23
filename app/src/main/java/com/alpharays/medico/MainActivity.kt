package com.alpharays.medico

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.alpharays.medico.domain.usecase.AuthUseCase
import com.alpharays.medico.presentation.navigation.Navigation
import com.alpharays.medico.presentation.theme.MedicoTheme
import com.alpharays.medico.profile.v2.di.DependencyProviderProfile
import com.alpharays.medico.profile.v2.navigation.ProfileFeatureImpl
import com.alpharays.medico.v2.di.DependencyProvider
import com.alpharays.medico.v2.modules.auth.navigation.AuthFeatureImpl
import com.alpharays.medico.v2.modules.home.navigation.HomeFeatureImpl
import com.alpharays.medico.v2.presentation.AppContent
import com.alpharays.mymedicommfma.communityv2.MedCommRouter
import com.alpharays.mymedicommfma.communityv2.navigation.CommunityFeatureImpl
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var authUseCase: AuthUseCase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DependencyProvider.provideImpl(
            homeFeatureApi = HomeFeatureImpl(),
            communityFeatureApi = CommunityFeatureImpl(MedicoApp.getInstance()),
            authFeatureApi = AuthFeatureImpl(authUseCase)
        )

        DependencyProviderProfile.provideImpl(
            profileFeatureApi = ProfileFeatureImpl()
        )

        //initialize medcomm app
        MedCommRouter.initiateMedCommRouter(context = this)

        setContent {
            MedicoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContent()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MedicoPreview() {
    Navigation()
}