## MQTT在Android上的二次封装
[ ![Download](https://api.bintray.com/packages/fungo/maven/android-mqtt/images/download.svg) ](https://bintray.com/fungo/maven/android-mqtt/_latestVersion)

#### 添加依赖
* 在project的build.gradle文件中添加maven仓库


      repositories {
          maven {
              url "https://repo.eclipse.org/content/repositories/paho-snapshots/"
          }
      }

* 在app的build.gradle文件中添加依赖

      dependencies {
          implementation 'com.pingerx:android-mqtt:1.0.4'
      }

#### 使用流程

* 初始化MQTT

      MqttManager.getInstance().init(context,config)


* 连接MQTT服务端

      MqttManager.getInstance().connect {
          onConnectSuccess {
              showTips("服务器连接成功")
          }
          onConnectFailed {
              showTips("服务器连接失败：${it?.message}")
          }
      }


* 订阅一个话题

      MqttManager.getInstance().subscribe(topic) {
          onSubscriberSuccess {
              showTips("订阅成功")
          }
          onSubscriberFailed {
              showTips("订阅失败：${it?.message}")
          }
          onDeliveryComplete {
              showTips("消息推送完毕：$it")
          }
          onConnectionLost {
              showTips("连接已断开")
          }
      }

* 推送一条消息

      MqttManager.getInstance().publishMessage(topic, "Hello Mqtt...")

* 主动断开连接

      MqttManager.getInstance().disconnect()

* 关闭MQTT客户端（一般app退出时调用）

      MqttManager.getInstance().close()


### 相关学习资料
* [官方Android项目地址](https://github.com/eclipse/paho.mqtt.android)
* [什么是MQTT](https://www.ibm.com/developerworks/cn/iot/iot-mqtt-why-good-for-iot/index.html)
* [一文读懂MQTT协议](https://blog.csdn.net/aa1215018028/article/details/84888096)
* [MQTT比TCP协议好在哪儿？](https://www.zhihu.com/question/23373904)
* [MQTT搭建推送服务](https://www.jianshu.com/p/b47fae7a654e)
