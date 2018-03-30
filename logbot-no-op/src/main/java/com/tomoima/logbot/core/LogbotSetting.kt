package com.tomoima.logbot.core

import java.io.Serializable


data class LogbotSettings(
        val bufferSize: Int = 0
): Serializable {

    fun createBuilder() = Builder(this)

    class Builder(private val logBotSettings: LogbotSettings = LogbotSettings())
}