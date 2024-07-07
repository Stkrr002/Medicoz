package com.alpharays.medico.profile.profile_utils.util

import android.util.Log
import com.alpharays.medico.profile.profile_utils.util.ProfileConstants.INTERNAL_SERVER_ERROR
import com.alpharays.medico.profile.profile_utils.util.ProfileConstants.SOMETHING_WENT_WRONG
import retrofit2.Response

class ResponseHandler {
    suspend fun <T : Any> callAPI(call: suspend () -> Response<T>): ResponseResult<T> {
        return try {
            val apiResponse = call()
//            Log.e("apiResponseChecking: ", apiResponse.toString())
            if (apiResponse.isSuccessful && apiResponse.body() != null) {
                ResponseResult.Success(apiResponse.body()!!)
            } else {
                val errorObj = apiResponse.errorBody()!!.charStream().readText()
                ResponseResult.Error(INTERNAL_SERVER_ERROR)
            }
        } catch (e: Exception) {
            Log.e("checkingException", e.message.toString())
            ResponseResult.Error(SOMETHING_WENT_WRONG)
        }
    }
}
