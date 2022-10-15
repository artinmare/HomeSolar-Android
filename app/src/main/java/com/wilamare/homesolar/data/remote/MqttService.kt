package com.wilamare.homesolar.data.remote

import android.content.Context
import com.wilamare.homesolar.common.Resource
import com.wilamare.homesolar.domain.model.*
import kotlinx.coroutines.flow.Flow
import java.util.*

interface MqttService {
    suspend fun observeConfigurations(): Flow<Configuration>
    suspend fun observeSetting(): Flow<Setting>
    suspend fun observeSummary(): Flow<Summary>
    suspend fun observeBMS(): Flow<BMS>
    suspend fun observeResponse(): Flow<Response>

    suspend fun initSession(
        context: Context,
        username: String,
        password: String,
        url: String,
        port: String
    ):Resource<Unit>
    suspend fun closeSession()
    suspend fun connect(): Resource<Unit>

    suspend fun publishMessage(topic:String, payload:String, qos: Int = 2, retain: Boolean = false)
    fun subscribe(topic: String)

    fun isConnected(): Boolean

    companion object {
        const val CLIENT_ID = "AndroidClient"
        private const val BASE_TOPIC = "homesolar"
        const val SENSOR_TOPIC = "$BASE_TOPIC/sensor"
        const val ACTUATOR_TOPIC = "$BASE_TOPIC/actuator"
        const val CLIENT_TOPIC = "$BASE_TOPIC/client"
        const val CLIENT_STATUS_TOPIC = "$CLIENT_TOPIC/+/status"
        const val SUMMARY_TOPIC = "$BASE_TOPIC/summary"
        const val BMS_TOPIC = "$BASE_TOPIC/bms"

        fun getReason(code: Int): String = when(code){
            0 ->
                "Server unreachable, check your inputs or internet connection!"
            1 ->
                "Protocol version is not supported by the server!"
            2 ->
                "Invalid Client ID!"
            3 ->
                "Broker is not available!"
            4 ->
                "Authentication failed, bad username or password!"
            5 ->
                "Action not authorized, contact the administrator!"
            else -> {
                "Unknown Error!"
            }
        }
    }
}