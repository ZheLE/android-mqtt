## MQTT在Android上的二次封装
[![](https://www.jitpack.io/v/PingerOne/MqttAndroid.svg)](https://www.jitpack.io/#PingerOne/MqttAndroid)

#### 添加依赖
* 在project的build.gradle文件中添加maven仓库


      repositories {
          maven {
              url "https://repo.eclipse.org/content/repositories/paho-snapshots/"
          }
      }



* 在app的build.gradle文件中添加依赖

      dependencies {
          implementation 'com.github.PingerOne:MqttAndroid:1.0.0'
      }

#### 使用流程

* 初始化MQTT

      MqttManager.getInstance().init(context)


* 连接MQTT服务端

      MqttManager.getInstance().connect(object : MqttSubscriber() {
          override fun onConnectSuccess() {
              showTips("服务器连接成功")
          }

          override fun onConnectFailed(throwable: Throwable) {
              showTips("服务器连接失败：${throwable.message}")
          }
      })


* 订阅一个话题

      MqttManager.getInstance().subscribe(subscriptionTopic, object : MqttSubscriber() {
          override fun onSubscriberSuccess() {
              showTips("订阅成功")
          }

          override fun onSubscriberFailed(exception: Throwable) {
              showTips("订阅失败：${exception.message}")
          }

          override fun onDeliveryComplete(message: String?) {
              showTips("消息推送完毕：$message")
          }

          override fun onConnectionLost(throwable: Throwable) {
              showTips("连接已断开")
          }
      })

* 推送一条消息

      MqttManager.getInstance().publishMessage(publishTopic, "Hello Mqtt...")


### 相关学习资料
* [官方Android项目地址](https://github.com/eclipse/paho.mqtt.android)
* [什么是MQTT](https://www.ibm.com/developerworks/cn/iot/iot-mqtt-why-good-for-iot/index.html)
* [一文读懂MQTT协议](https://blog.csdn.net/aa1215018028/article/details/84888096)
* [MQTT比TCP协议好在哪儿？](https://www.zhihu.com/question/23373904)
* [MQTT搭建推送服务](https://www.jianshu.com/p/b47fae7a654e)
