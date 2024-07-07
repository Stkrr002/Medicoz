package com.alpharays.medico.medico_utils

import android.util.Log
import com.alpharays.medico.medico_utils.MedicoConstants.INTERNAL_SERVER_ERROR
import com.alpharays.medico.medico_utils.MedicoConstants.SOMETHING_WENT_WRONG
import retrofit2.Response

//class ResponseHandler {
//    suspend fun <T : Any> callAPI(call: suspend () -> Response<T>): ResponseResult<T> {
//        return try {
//            val apiResponse = call()
////            Log.e("apiResponseChecking: ", apiResponse.toString())
//            if (apiResponse.isSuccessful && apiResponse.body() != null) {
//                ResponseResult.Success(apiResponse.body()!!)
//            } else {
//                val errorObj = apiResponse.errorBody()!!.charStream().readText()
//                ResponseResult.Error(INTERNAL_SERVER_ERROR)
//            }
//        } catch (e: Exception) {
//            Log.e("checkingException", e.message.toString())
//            ResponseResult.Error(SOMETHING_WENT_WRONG)
//        }
//    }
//}
