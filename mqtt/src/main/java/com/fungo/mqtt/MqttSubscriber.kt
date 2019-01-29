package com.fungo.mqtt


/**
 * @author Pinger
 * @since 2019/1/18 11:26
 *
 * 订阅者简单回调类
 */
open class MqttSubscriber : IMqttSubscriber {

    override fun onMessageArrived(topic: String, message: String?, qos: Int) {
    }

    override fun onDeliveryComplete(message: String?) {
    }

    override fun onConnectSuccess() {
    }

    override fun onConnectionLost(throwable: Throwable?) {
    }

    override fun onConnectFailed(throwable: Throwable?) {
    }

    override fun onSubscriberSuccess() {
    }

    override fun onSubscriberFailed(exception: Throwable?) {
    }
}