package com.fungo.mqtt


/**
 * @author Pinger
 * @since 2019/1/18 11:26
 *
 * 订阅者简单回调类
 */
open class MqttSubscriber : IMqttSubscriber {

    var messageArrived: ((topic: String, message: String?, qos: Int) -> Unit)? = null
    var deliveryComplete: ((message: String?) -> Unit)? = null
    var connectSuccess: (() -> Unit)? = null
    var connectLost: ((throwable: Throwable?) -> Unit)? = null
    var connectFailed: ((throwable: Throwable?) -> Unit)? = null
    var subscriberFailed: ((throwable: Throwable?) -> Unit)? = null
    var subscriberSuccess: (() -> Unit)? = null

    override fun onMessageArrived(messageArrived: (topic: String, message: String?, qos: Int) -> Unit) {
        this.messageArrived = messageArrived
    }

    override fun onDeliveryComplete(deliveryComplete: (message: String?) -> Unit) {
        this.deliveryComplete = deliveryComplete
    }

    override fun onConnectSuccess(connectSuccess: () -> Unit) {
        this.connectSuccess = connectSuccess
    }

    override fun onConnectionLost(connectLost: (throwable: Throwable?) -> Unit) {
        this.connectLost = connectLost
    }

    override fun onConnectFailed(connectFailed: (throwable: Throwable?) -> Unit) {
        this.connectFailed = connectFailed
    }

    override fun onSubscriberSuccess(subscriberSuccess: () -> Unit) {
        this.subscriberSuccess = subscriberSuccess
    }

    override fun onSubscriberFailed(subscriberFailed: (exception: Throwable?) -> Unit) {
        this.subscriberFailed = subscriberFailed
    }
}