package com.alpharays.medico.profile.profile_utils.util

import android.content.Context
import android.widget.Toast

class MedicoToast {
    companion object {
        fun showToast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
}