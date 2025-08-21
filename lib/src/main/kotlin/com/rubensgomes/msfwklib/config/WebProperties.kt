/*
 * Copyright 2025 Rubens Gomes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rubensgomes.msfwklib.config

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.HttpMethod

/**
 * A property class to hold the list of allowed HTTP methods. Every application must configure its
 * own list in the Spring `application.yml` property `app.web.allowed-methods`.
 *
 * Note that `@EnableConfigurationProperties(value = [WebProperties::class])` must be annotated in
 * the `@SpringBootApplication` class.
 *
 * @author Rubens Gomes
 */
@ConfigurationProperties(prefix = "app.web")
data class WebProperties(val allowedMethods: List<String> = listOf(HttpMethod.GET.name())) {
    init {
        // run checks to ensure the "app.web.allowed-methods" is valid
        if (allowedMethods.isEmpty()) {
            val msg = "Spring configuration property [app.web.allowed-methods] must not be empty"
            log.error(msg)
            throw IllegalArgumentException(msg)
        }

        for (method in allowedMethods) {
            val httpMethod = HttpMethod.valueOf(method)
            when (httpMethod) {
                in HttpMethod.values() -> log.debug("HTTP method [$method] is valid")
                else -> {
                    log.error("HTTP method [$method] is not valid")
                    throw IllegalArgumentException("HTTP method [$method] is not valid")
                }
            }
        }
    }

    internal companion object {
        private val log = LoggerFactory.getLogger(WebProperties::class.java)
    }
}
