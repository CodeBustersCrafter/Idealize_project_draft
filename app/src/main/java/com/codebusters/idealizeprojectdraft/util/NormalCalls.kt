package com.codebusters.idealizeprojectdraft.util

import android.content.Intent
import android.net.Uri


class NormalCalls(var number: String) {
    fun call(): Intent {
        val calling = Intent(Intent.ACTION_CALL)
        calling.setData(Uri.parse("tel:$number"))
        return calling
    }
}