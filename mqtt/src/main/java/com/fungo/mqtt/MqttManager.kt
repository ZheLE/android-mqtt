package com.fungo.mqtt

import android.content.Context
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*


/**
 * @author Pinger
 * @since 2019/1/15 20:06
 *
 * 消息队列包装代理,并不是代理模式
 */
class MqttManager {

    private val baseUrl = "tcp://iot.eclipse.org:1883"
    private val userName = "admin"
    private val password = "password"
    private val clientId = "MqttAndroidClient"

    private var mqttClient: MqttAndroidClient? = null
    private val mSubscribers = LinkedHashMap<String, IMqttSubscriber>()

    fun init(context: Context) {
        mqttClient = MqttAndroidClient(context, baseUrl, clientId)
        mqttClient!!.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                if (reconnect) {
                    LogUtils.d("----> MQTT重新连接完成, serverURI = $serverURI")
                } else {
                    LogUtils.d("----> MQTT连接完成, serverURI = $serverURI")
                }
            }

            override fun connectionLost(cause: Throwable?) {
                mSubscribers.entries.forEach {
                    it.value.onConnectionLost(cause)
                }
                LogUtils.d("----> MQTT断开连接, cause = ${cause?.message}")
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                val subscriber = mSubscribers[topic]
                subscriber?.onMessageArrived(topic, String(message.payload), message.qos)
                LogUtils.d("----> MQTT消息到达, topic = $topic, message = ${String(message.payload)}")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                mSubscribers.entries.forEach {
                    it.value.onDeliveryComplete(token.message.toString())
                }
                LogUtils.d("----> MQTT消息发送完毕, token = ${token.message}")
            }
        })
    }

    /**M
     * 连接服务器
     */
    fun connect(subscriber: IMqttSubscriber? = null) {
        if (mqttClient == null) {
            LogUtils.e("----> MQTT连接失败, 请先初始化MQTT")
            return
        }
        try {
            mqttClient?.connect(generateConnectOptions(), null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    subscriber?.onConnectSuccess()
                    mSubscribers.entries.forEach {
                        it.value.onConnectSuccess()
                    }
                    LogUtils.d("----> MQTT响应成功")
                    val disconnectedBufferOptions = DisconnectedBufferOptions()
                    disconnectedBufferOptions.isBufferEnabled = true
                    disconnectedBufferOptions.bufferSize = 100
                    disconnectedBufferOptions.isPersistBuffer = false
                    disconnectedBufferOptions.isDeleteOldestMessages = false
                    mqttClient?.setBufferOpts(disconnectedBufferOptions)
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable?) {
                    subscriber?.onConnectFailed(exception)
                    mSubscribers.entries.forEach {
                        it.value.onConnectFailed(exception)
                    }
                    LogUtils.d("----> MQTT连接失败, exception = ${exception?.message}")
                }
            })
        } catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }

    /**
     * 订阅一个话题
     */
    fun subscribe(topic: String, subscriber: IMqttSubscriber) {
        if (mqttClient == null) {
            LogUtils.e("----> MQTT订阅失败, 请先初始化MQTT")
            return
        }
        if (!isConnected()) {
            LogUtils.e("----> MQTT订阅失败, 请先连接服务器")
            return
        }
        mSubscribers[topic] = subscriber
        try {
            mqttClient?.subscribe(topic, 0, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    subscriber.onSubscriberSuccess()
                    LogUtils.d("----> MQTT订阅成功, topic = $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    subscriber.onSubscriberFailed(exception)
                    LogUtils.d("----> MQTT订阅失败, exception = ${exception.message}")
                }
            })
        } catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }

    /**
     * 发布消息
     */
    fun publishMessage(topic: String, content: String) {
        if (mqttClient == null) {
            LogUtils.e("----> MQTT发布消息失败, 请先初始化MQTT")
            return
        }
        if (!isConnected()) {
            LogUtils.e("----> MQTT发布消息失败, 请先连接服务器")
            return
        }
        try {
            val message = MqttMessage()
            message.payload = content.toByteArray()
            mqttClient?.publish(topic, message)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    /**
     * 断开连接
     */
    fun disconnect() {
        try {
            mqttClient?.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * 关闭MQTT客户端，再次使用需要重新创建
     */
    fun close() {
        try {
            mqttClient?.disconnect()
            mqttClient?.unregisterResources()
            mqttClient?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 判断连接是否断开
     */
    fun isConnected(): Boolean {
        try {
            return mqttClient?.isConnected ?: false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun getServerUrl(): String {
        return baseUrl
    }

    fun getSubscribers(): LinkedHashMap<String, IMqttSubscriber> {
        return mSubscribers
    }

    /**
     * 生成默认的连接配置
     */
    private fun generateConnectOptions(): MqttConnectOptions {
        val options = MqttConnectOptions()
        options.isAutomaticReconnect = true
        options.isCleanSession = false
        options.userName = userName
        options.password = password.toCharArray()
        return options
    }

    companion object {
        fun getInstance(): MqttManager {
            return Holder.mInstance
        }
    }

    object Holder {
        val mInstance = MqttManager()
    }
}