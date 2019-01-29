package com.fungo.mqtt


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
     * @param topic   主题
     * @param message 消息内容
     * @param qos     消息策略
     */
    fun onMessageArrived(topic: String, message: String?, qos: Int)


    /**
     * 消息发送完成
     */
    fun onDeliveryComplete(message: String?)

    /**
     * 服务器连接成功
     */
    fun onConnectSuccess()

    /**
     * 服务器连接断开
     */
    fun onConnectionLost(throwable: Throwable?)

    /**
     * 服务器连接失败
     */
    fun onConnectFailed(throwable: Throwable?)

    /**
     * 订阅成功
     */
    fun onSubscriberSuccess()

    /**
     * 订阅失败
     */
    fun onSubscriberFailed(exception: Throwable?)
}