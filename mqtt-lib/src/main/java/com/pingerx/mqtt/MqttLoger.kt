package com.pingerx.mqtt

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

/**
 * @author Pinger
 * @since 2018/4/27 21:53
 */
object MqttLoger {

    private var DEBUG = true
    private const val TAG = "mqtt"
    private const val MIN_STACK_OFFSET = 3
    private const val METHOD_COUNT = 3
    private const val JSON_INDENT = 4

    fun i(msg: Int) {
        i(msg.toString())
    }

    fun i(msg: String?) {
        log(Log.INFO, msg ?: "")
    }

    fun i(args: Array<String>) {
        try {
            val stringBuilder = StringBuilder()
            for (arg in args) {
                stringBuilder.append(arg)
                stringBuilder.append(",")
            }
            i(stringBuilder.toString())
        } catch (throwable: Throwable) {
            e(throwable)
        }

    }

    fun e(msg: String?) {
        log(Log.ERROR, msg)
    }

    fun e(msg: Int) {
        log(Log.ERROR, msg.toString())
    }

    fun e(tag: String, msg: String?) {
        log(tag, Log.ERROR, msg)
    }

    fun e(throwable: Throwable?) {
        if (throwable != null) {
            e(throwable.message)
            throwable.printStackTrace()
        }
    }

    fun e(exception: Exception?) {
        if (exception != null) {
            e(exception.message)
            exception.printStackTrace()
        }
    }

    fun d(msg: String?) {
        log(Log.DEBUG, msg)
    }

    fun d(msg: Int) {
        log(Log.DEBUG, msg.toString())
    }

    fun d(tag: String, msg: String?) {
        log(tag, Log.DEBUG, msg)
    }


    fun d(throwable: Throwable?) {
        if (throwable != null) {
            e(throwable.message)
            throwable.printStackTrace()
        }
    }

    fun json(json: String?) {
        try {
            var message = json
            if (json == null || json.isEmpty()) {
                message = ""
            } else if (json.startsWith("{")) {
                val jsonObject = JSONObject(json)
                message = jsonObject.toString(JSON_INDENT)
            } else if (json.startsWith("[")) {
                val jsonArray = JSONArray(json)
                message = jsonArray.toString(JSON_INDENT)
            }
            i(message)
        } catch (throwable: Throwable) {
            e(throwable.cause?.message + "\n" + json)
        }
    }


    fun print(msg: String?) {
        if (DEBUG) {
            println(msg)
        }
    }

    private fun log(logType: Int, msg: String?) {
        log(TAG, logType, msg, false)
    }

    private fun log(tag: String, logType: Int, msg: String?) {
        log(tag, logType, msg, false)
    }

    private fun log(tag: String, logType: Int, msg: String?, showHeader: Boolean) {
        if (DEBUG) {
            if (showHeader) {
                logHeaderContent()
            }

            var content = "| $msg"
            val bytes = content.toByteArray()
            val length = bytes.size

            var i = 0
            while (i < length) {
                val count = Math.min(length - i, 4000)
                content = String(bytes, i, count)

                when (logType) {
                    Log.ERROR -> Log.e(tag, content)
                    Log.INFO -> Log.i(tag, content)
                    Log.DEBUG -> Log.d(tag, content)
                    else -> Log.v(tag, content)
                }
                i += 4000
            }
        }
    }


    private fun logHeaderContent() {
        try {
            val trace = Thread.currentThread().stackTrace
            val stackOffset = getStackOffset(trace)

            var stackEnd = stackOffset + METHOD_COUNT
            if (stackEnd >= trace.size) {
                stackEnd = trace.size - 1
            }

            Log.d(TAG, "| Thread: " + Thread.currentThread().name)

            for (i in stackEnd downTo stackOffset) {

                val builder = StringBuilder()
                builder.append("| ")
                    .append(getSimpleClassName(trace[i].className))
                    .append(".")
                    .append(trace[i].methodName)
                    .append(" ")
                    .append(" (")
                    .append(trace[i].fileName)
                    .append(":")
                    .append(trace[i].lineNumber)
                    .append(")")
                Log.d(TAG, builder.toString())
            }
        } catch (throwable: Throwable) {
            e(throwable)
        }

    }

    private fun getStackOffset(trace: Array<StackTraceElement>): Int {
        var start = -1

        for (i in MIN_STACK_OFFSET until trace.size) {
            val element = trace[i]
            val name = element.className

            if (name != MqttLoger::class.java.name) {
                start = i
                break
            }
        }
        return start
    }

    private fun getSimpleClassName(name: String): String {
        val lastIndex = name.lastIndexOf(".")
        return name.substring(lastIndex + 1)
    }

    fun setDebug(isDebug: Boolean) {
        DEBUG = isDebug
    }
}