package com.darkube.pirate.services

import android.app.Application
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.darkube.pirate.config.SERVER_URL
import com.darkube.pirate.models.MainViewModel
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URISyntaxException

object SocketManager : DefaultLifecycleObserver {
    private var socket: Socket? = null
    private var pirateId: String? = null

    fun initialize(application: Application, userId: String) {
        pirateId = userId
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun connect(flag: Boolean = true) {
        if (socket != null) {
            return
        }
        try {
            val body = JSONObject().put("pirateId", pirateId)
            if (flag) {
                body.put("status", "OFFLINE")
            }
            socket = IO.socket(SERVER_URL).apply {
                on(Socket.EVENT_CONNECT) {
                    emit("init", body)
                    CoroutineScope(Dispatchers.IO).launch {
                        if (MainViewModel.getCurrentPirateId().isNotEmpty()) {
                            delay(500)
                            enterChatRoute(MainViewModel.getCurrentPirateId())
                        }
                    }
                    Log.d("Socket.IO", "Connected to server")
                }
                connect()
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    private fun disconnect() {
        if (MainViewModel.getCurrentPirateId().isNotEmpty()) {
            exitChatRoute(MainViewModel.getCurrentPirateId())
        }
        socket?.disconnect()
        socket = null
    }

    fun enterChatRoute(otherPirateId: String) {
        Log.d("Socket.IO", "Enter Chat")
        socket?.emit(
            "enter-chat",
            JSONObject().put("otherPirateId", otherPirateId)
        )
        Log.d("Socket.IO", "Sent is-online check for: $otherPirateId")
        socket?.off("user-online-response")
        socket?.on("user-online-response") { args ->
            val response = args.getOrNull(0) as? JSONObject
            val isOnline = response?.optBoolean("isOnline") ?: false
            MainViewModel.setOtherUserOnline(isOnline)
        }
        socket?.off("typing-changed")
        socket?.on("typing-changed") { args ->
            val response = args.getOrNull(0) as? JSONObject
            val isTyping = response?.optBoolean("isTyping") ?: false
            val receiverPirateId = response?.optString("otherPirateId") ?: ""
            if (receiverPirateId == otherPirateId) {
                MainViewModel.setOtherUserTyping(isTyping)
            }
        }
    }

    fun exitChatRoute(otherPirateId: String) {
        Log.d("Socket.IO", "Exit Chat")
        socket?.emit(
            "exit-chat",
            JSONObject().put("otherPirateId", otherPirateId)
        )
        socket?.off("user-online-response")
        socket?.off("typing-changed")
    }

    fun startedTyping() {
        socket?.emit("started-typing", JSONObject().put("debug", ""))
    }

    fun stoppedTyping() {
        socket?.emit("stopped-typing", JSONObject().put("debug", ""))
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        MainViewModel.setAppInForeground(true)
        connect(MainViewModel.hideOnlineStatus())
        Log.d("Socket.IO", "App is in foreground")
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        MainViewModel.setAppInForeground(false)
        disconnect()
        Log.d("Socket.IO", "App is in background")
    }
}
