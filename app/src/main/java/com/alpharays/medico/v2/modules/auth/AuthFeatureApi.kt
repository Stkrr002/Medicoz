package com.alpharays.medico.v2.modules.auth

import com.alpharays.alaskagemsdk.core.FeatureApi

interface AuthFeatureApi : FeatureApi {
    val authRoute: String
}