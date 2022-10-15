package com.wilamare.homesolar.data.remote

import android.content.Context
import android.util.Log
import com.wilamare.homesolar.common.Resource
import com.wilamare.homesolar.data.remote.dto.SummaryDto
import com.wilamare.homesolar.domain.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.util.*
import kotlin.coroutines.suspendCoroutine

class MqttServiceImpl: MqttService {
    private lateinit var configurationCallback: Callback
    private lateinit var settingCallback: Callback
    private lateinit var bmsCallback: Callback
    private lateinit var responseCallback: Callback
    private lateinit var summaryCallback: Callback

    private lateinit var client: MqttAndroidClient
    private var username:String = ""
    private var password:String = ""
    private var clientID:String = ""

    override suspend fun observeConfigurations(): Flow<Configuration> {
        TODO("Not yet implemented")
    }

    override suspend fun observeSetting(): Flow<Setting> {
        TODO("Not yet implemented")
    }

    override suspend fun observeSummary(): Flow<Summary> = callbackFlow {
        summaryCallback = object : Callback {
            override fun onMessageArrived(message: String) {
                Log.w("MqttServiceImpl", "onMessageArrived#Summary")
                val summaryDto = Json.decodeFromString<SummaryDto>(message)
                trySend(summaryDto.toSummary())
            }
        }
        awaitClose {
            emptyFlow<Summary>()
            Log.w("MqttServiceImpl", "awaitClose#Summary")
        }
    }

    override suspend fun observeBMS(): Flow<BMS> {
        TODO("Not yet implemented")
    }

    override suspend fun observeResponse(): Flow<Response> {
        TODO("Not yet implemented")
    }

    override suspend fun initSession(
        context: Context,
        username: String,
        password: String,
        url: String,
        port: String
    ): Resource<Unit> {
        return try {
            val uuid = UUID.nameUUIDFromBytes(username.toByteArray()).toString()
            this.username = username
            this.password = password
            this.clientID = "${MqttService.CLIENT_ID}-$uuid"
            Log.w("MqttServiceImpl", "UUID: $uuid")
            client = MqttAndroidClient(context, "tcp://$url:$port", clientID)
            client.setCallback(object : MqttCallbackExtended{
                override fun connectionLost(cause: Throwable?) {
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    Log.w("MqttServiceImpl", "MessageArrived: $topic#$message")
                    val json = message.toString()
                    topic?.let {
                        when(it){
                            MqttService.SUMMARY_TOPIC -> {
                                summaryCallback.onMessageArrived(json)
                            }
                            else -> {

                            }
                        }
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                }

                override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                    if(reconnect)
                        Log.w("MqttServiceImpl", "Reconnected to $serverURI")
                    client.publish(MqttService.CLIENT_STATUS_TOPIC.replace("+",clientID), "Connected".toByteArray(), 2, true)
                }

            })
            connect()
        } catch (e: Exception) {
            Log.e("MqttServiceImpl", "Something went wrong when trying to initialize Session", e)
            Resource.Error(e.localizedMessage ?: "Unknown Error")
        }
    }

    override suspend fun connect(): Resource<Unit> = suspendCancellableCoroutine { cont ->
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.connectionTimeout = 5
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = false
        mqttConnectOptions.userName = username
        mqttConnectOptions.password = password.toCharArray()
        mqttConnectOptions.setWill(
            MqttService.CLIENT_STATUS_TOPIC.replace("+",clientID),
            "Disconnected Unexpectedly".toByteArray(),
            2,
            true
        )

        client.connect(mqttConnectOptions, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                Log.w("MqttServiceImpl", "Connected Successfully")
                val disconnectedBufferOptions = DisconnectedBufferOptions()
                disconnectedBufferOptions.isBufferEnabled = true
                disconnectedBufferOptions.bufferSize = 100
                disconnectedBufferOptions.isPersistBuffer = false
                disconnectedBufferOptions.isDeleteOldestMessages = false
                client.setBufferOpts(disconnectedBufferOptions)

                subscribe(MqttService.SUMMARY_TOPIC)
                subscribe(MqttService.BMS_TOPIC)

                client.publish(MqttService.CLIENT_STATUS_TOPIC.replace("+",clientID), "Connected".toByteArray(), 2, true)

                if(cont.isActive){
                    cont.resumeWith(
                        Result.success(
                            Resource.Success(Unit)
                        )
                    )
                }
            }

            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                exception.printStackTrace()
                if(cont.isActive){
                    cont.resumeWith(
                        Result.success(
                            if(exception is MqttException) Resource.Error(MqttService.getReason(exception.reasonCode)) else Resource.Error(exception.cause?.toString() ?: "Unknown Error")
                        )
                    )
                }
            }
        })
    }

    override suspend fun publishMessage(topic: String, payload: String, qos: Int, retain: Boolean) {
        try {
            client.publish(topic, payload.toByteArray(), qos, retain)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun subscribe(topic: String) {
        try {
            client.subscribe(topic, 0, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.w("MqttServiceImpl", "Subscribed to ${topic}!")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.w("MqttServiceImpl", "Subscribed fail! #${topic}")
                }
            })
        } catch (ex: MqttException) {
            Log.e("MqttServiceImpl", "Something went wrong whilst subscribing!", ex)
        }
    }

    override suspend fun closeSession() {
        client.disconnect()
    }

    override fun isConnected(): Boolean {
        return try {
            client.isConnected
        } catch(e: Exception) {
            false
        }
    }
}