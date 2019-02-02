package com.pingerx.mqtt

/**
 * @author Pinger
 * @since 2019/2/2 15:06
 */
class MqttConfig {

    private var baseUrl = "tcp://iot.eclipse.org:1883"
    private var userName = "admin"
    private var password = "password"
    private var clientId = "MqttAndroidClient"

    fun create(): MqttConfig {
        return this
    }

    fun setBaseUrl(baseUrl: String): MqttConfig {
        this.baseUrl = baseUrl
        return this
    }

    fun setUserName(userName: String): MqttConfig {
        this.userName = userName
        return this
    }

    fun setPassword(password: String): MqttConfig {
        this.password = password
        return this
    }

    fun setClientId(clientId: String): MqttConfig {
        this.clientId = clientId
        return this
    }

    fun getBaseUrl(): String {
        return this.baseUrl
    }

    fun getUserName(): String {
        return this.userName
    }

    fun getPassword(): String {
        return password
    }

    fun getClientId(): String {
        return clientId
    }
}