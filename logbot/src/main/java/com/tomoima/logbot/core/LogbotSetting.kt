package com.tomoima.logbot.core

import java.io.Serializable


data class LogbotSettings(
        val bufferSize: Int = Consts.DEFAULT_LOG_BUFFER
): Serializable {

    fun createBuilder() = Builder(this)

    class Builder(private val logBotSettings: LogbotSettings = LogbotSettings()) {
        fun bufferSize(size: Int) = apply { logBotSettings.copy(bufferSize = size) }
        fun build() = logBotSettings
    }
}