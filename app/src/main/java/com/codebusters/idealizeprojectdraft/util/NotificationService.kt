package com.codebusters.idealizeprojectdraft.util

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.codebusters.idealizeprojectdraft.R
import com.codebusters.idealizeprojectdraft.RequestActivity
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class NotificationService : Service() {

    // Notification channel ID
    private val channelId = "leaderboard_channel"

    // Notification ID
    private var notificationId = 123
    private val myTags = MyTags()


    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        //Create a notification channel
        createNotificationChannel()

        FirebaseAuth.getInstance().currentUser?.let { user ->
            FirebaseFirestore.getInstance().collection(myTags.users).document(user.uid)
                .collection(myTags.userRequests)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }

                    for (dc in snapshots!!.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            // Handle the new request here
                            // Build the notification
                            FirebaseFirestore.getInstance().collection(myTags.users).document(user.uid)
                                .get().addOnSuccessListener { document ->
                                    if (document.get(myTags.areNewRequests) != null) {
                                        if (document.get(myTags.areNewRequests).toString().trim().toInt()==1) {
                                            FirebaseFirestore.getInstance().collection(myTags.ads).document(dc.document.get(myTags.requestAdID).toString())
                                                .get().addOnSuccessListener { documentSnapshot ->
                                                    notificationId++
                                                    val notification = buildNotification(documentSnapshot.get(myTags.adName).toString(),notificationId)

                                                    // Start the foreground service
                                                    //startForeground(notificationId, notification)
                                                    val notificationManager = getSystemService(
                                                        Context.NOTIFICATION_SERVICE) as NotificationManager
                                                    notificationManager.notify(notificationId, notification)

                                                }
                                        }
                                    }
                                }

                        }
                    }
                }
        }
        // Return START_STICKY to ensure the service is restarted if it is killed
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Requests Updates"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

    private fun buildNotification(name : String,id : Int): Notification {
        val notificationTitle = "New Request"
        val notificationText = "Check out the latest requests!\n$name"
        val notificationIntent= Intent(this, RequestActivity::class.java)
        notificationIntent.putExtra(myTags.intentUID, FirebaseAuth.getInstance().currentUser?.uid)
        notificationIntent.putExtra(myTags.intentFragmentRequest, "1")
        notificationIntent.putExtra(myTags.intentNotificationID, id.toString())
        val pendingIntent = PendingIntent.getActivity(this,
            id,
            notificationIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)


        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.logo1)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
    }
}