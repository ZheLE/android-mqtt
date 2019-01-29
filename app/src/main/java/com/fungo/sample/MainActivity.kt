package com.fungo.sample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fungo.mqtt.MqttManager
import com.fungo.mqtt.MqttSubscriber
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val subscriptionTopic = "AndroidTopic"
    private val publishTopic = "AndroidPublishTopic"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 初始化
        MqttManager.getInstance().init(this)

        showTips("服务器地址：${MqttManager.getInstance().getServerUrl()}")
    }

    // 连接服务端
    fun btnConnect(view: View) {
        println(view)

        MqttManager.getInstance().connect(object : MqttSubscriber() {
            override fun onConnectSuccess() {
                showTips("服务器连接成功")
            }

            override fun onConnectFailed(throwable: Throwable?) {
                showTips("服务器连接失败：${throwable?.message}")
            }
        })
    }

    // 订阅主题
    fun btnSubscribe(view: View) {
        println(view)
        MqttManager.getInstance().subscribe(subscriptionTopic, object : MqttSubscriber() {
            override fun onSubscriberSuccess() {
                showTips("订阅成功")
            }

            override fun onSubscriberFailed(exception: Throwable?) {
                showTips("订阅失败：${exception?.message}")
            }

            override fun onDeliveryComplete(message: String?) {
                showTips("消息推送完毕：$message")
            }

            override fun onConnectionLost(throwable: Throwable?) {
                showTips("连接已断开")
            }
        })
    }

    // 推送消息
    fun btnPublish(view: View) {
        println(view)
        MqttManager.getInstance().publishMessage(publishTopic, "Hello Mqtt...")
    }

    // 断开连接
    fun btnClose(view: View) {
        println(view)
        MqttManager.getInstance().disconnect()
    }

    private fun showTips(msg: String?) {
        tvMessage?.text = msg
    }


    override fun onDestroy() {
        super.onDestroy()
        MqttManager.getInstance().close()
    }
}
