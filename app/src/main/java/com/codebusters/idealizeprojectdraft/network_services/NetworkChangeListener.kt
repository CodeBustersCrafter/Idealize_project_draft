package com.codebusters.idealizeprojectdraft.network_services

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import com.codebusters.idealizeprojectdraft.R


class NetworkChangeListener : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val network = Network
        if (!network.isConnected(context)) {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.no_connection)
            dialog.setCancelable(false)
            val btn = dialog.findViewById<View>(R.id.btn_pop_no_connection_try) as Button
            btn.setOnClickListener {
                dialog.dismiss()
                onReceive(context, intent)
            }
            dialog.show()
        }
    }
}
