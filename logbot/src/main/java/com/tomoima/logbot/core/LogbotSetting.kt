/*
 * Copyright (C) 2018 Tomoaki Imai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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