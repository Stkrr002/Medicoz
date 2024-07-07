package com.alpharays.medico.medico_utils

import android.content.Context
import android.widget.Toast

class MedicoToast {
    companion object {
        fun showToast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
}