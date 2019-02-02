package com.pingerx.mqtt

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

    private var mConfig: MqttConfig? = null
    private var mqttClient: MqttAndroidClient? = null
    private val mSubscribers = LinkedHashMap<String, MqttSubscriber>()

    fun init(context: Context, config: MqttConfig) {
        mConfig = config
        mqttClient = MqttAndroidClient(context, config.getBaseUrl(), config.getClientId())
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
                    it.value.connectLost?.invoke(cause)
                }
                LogUtils.d("----> MQTT断开连接, cause = ${cause?.message}")
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                val subscriber = mSubscribers[topic]
                subscriber?.messageArrived?.invoke(topic, String(message.payload), message.qos)
                LogUtils.d("----> MQTT消息到达, topic = $topic, message = ${String(message.payload)}")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                mSubscribers.entries.forEach {
                    it.value.deliveryComplete?.invoke(token.message.toString())
                }
                LogUtils.d("----> MQTT消息发送完毕, token = ${token.message}")
            }
        })
    }

    /**M
     * 连接服务器
     * @param subscriber 表示当前方法的回调，并不会作用到全局
     */
    fun connect(subscriber: MqttSubscriber.() -> Unit?) {
        if (mqttClient == null) {
            LogUtils.e("----> MQTT连接失败, 请先初始化MQTT")
            return
        }
        val callback = MqttSubscriber()
        callback.subscriber()
        try {
            mqttClient?.connect(generateConnectOptions(), null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    callback.connectSuccess?.invoke()
                    mSubscribers.entries.forEach {
                        it.value.connectSuccess?.invoke()
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
                    callback.connectFailed?.invoke(exception)
                    mSubscribers.entries.forEach {
                        it.value.connectFailed?.invoke(exception)
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
    fun subscribe(topic: String, subscriber: MqttSubscriber.() -> Unit) {
        if (mqttClient == null) {
            LogUtils.e("----> MQTT订阅失败, 请先初始化MQTT")
            return
        }
        if (!isConnected()) {
            LogUtils.e("----> MQTT订阅失败, 请先连接服务器")
            return
        }

        val callback = MqttSubscriber()
        callback.subscriber()
        mSubscribers[topic] = callback
        try {
            mqttClient?.subscribe(topic, 0, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    callback.subscriberSuccess?.invoke()
                    LogUtils.d("----> MQTT订阅成功, topic = $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    callback.subscriberFailed?.invoke(exception)
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
     * 主动断开连接，不会自动重连
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

    fun getServerUrl(): String? {
        return mConfig?.getBaseUrl()
    }

    fun getSubscribers(): LinkedHashMap<String, MqttSubscriber> {
        return mSubscribers
    }

    /**
     * 生成默认的连接配置
     */
    private fun generateConnectOptions(): MqttConnectOptions {
        val options = MqttConnectOptions()
        options.isAutomaticReconnect = true
        options.isCleanSession = false
        options.userName = mConfig?.getUserName()
        options.password = mConfig?.getPassword()?.toCharArray()
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