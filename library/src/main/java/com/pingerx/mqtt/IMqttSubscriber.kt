package com.pingerx.mqtt

/**
 * @author Pinger
 * @since 2019/1/18 10:59
 *
 * MQTT订阅接口
 *
 */
interface IMqttSubscriber {

    /**
     * 收到消息
     *
     * @param messageArrived  形参函数
     */
    fun onMessageArrived(messageArrived: (topic: String, message: String?, qos: Int) -> Unit)


    /**
     * 消息发送完成
     */
    fun onDeliveryComplete(deliveryComplete: (message: String?) -> Unit)

    /**
     * 服务器连接成功
     */
    fun onConnectSuccess(connectSuccess: () -> Unit)

    /**
     * 服务器连接断开
     */
    fun onConnectionLost(connectLost: (throwable: Throwable?) -> Unit)

    /**
     * 服务器连接失败
     */
    fun onConnectFailed(connectFailed: (throwable: Throwable?) -> Unit)

    /**
     * 订阅成功
     */
    fun onSubscriberSuccess(subscriberSuccess: () -> Unit)

    /**
     * 订阅失败
     */
    fun onSubscriberFailed(subscriberFailed: (exception: Throwable?) -> Unit)
}