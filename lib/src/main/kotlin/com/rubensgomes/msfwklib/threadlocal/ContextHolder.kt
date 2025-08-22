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
package com.rubensgomes.msfwklib.threadlocal

import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.commons.lang3.Validate

private val log = KotlinLogging.logger {}

/**
 * `ContextHolder` is a singleton object that manages a thread-local context map. It allows storing
 * and retrieving key-value pairs specific to the current thread.
 *
 * The context is useful for passing contextual information (e.g., request IDs, user info) across
 * different parts of an application without using method parameters.
 *
 * @author Rubens Gomes
 */
object ContextHolder {
    // Thread-local storage for a mutable map of String key-value pairs
    private val contextMap = ThreadLocal<MutableMap<String, String>>()

    /**
     * Retrieves the value associated with the given key from the thread-local context.
     *
     * @param key the key to look up; must not be blank
     * @return the value associated with the key, or null if not present
     * @throws IllegalArgumentException if the key is blank
     */
    fun get(key: String): String? {
        Validate.notBlank(key, "Key must not be blank")
        log.debug { "Retrieving value for key: $key" }

        if (contextMap.get().isNullOrEmpty()) {
            log.info { "ThreadLocal context is empty" }
            return null
        }

        if (!contextMap.get().contains(key)) {
            log.info { "ThreadLocal does not contain the key: $key" }
            return null
        }

        val value = contextMap.get()[key]
        log.debug { "Retrieved value for key: $key = $value" }
        return value
    }

    /**
     * Stores a key-value pair in the thread-local context. If the key already exists, its value
     * will be overwritten.
     *
     * @param key the key to store; must not be blank
     * @param value the value to associate with the key
     * @throws IllegalArgumentException if the key is blank
     */
    fun put(key: String, value: String) {
        Validate.notBlank(key, "Key must not be blank")
        log.debug { "Putting value for key: $key = $value " }
        val map = contextMap.get() ?: mutableMapOf()

        if (map.containsKey(key)) {
            log.warn { "Overwriting existing value for key: $key" }
        }

        map[key] = value
        contextMap.set(map)
    }

    /**
     * Clears the thread-local context for the current thread. Removes all key-value pairs stored in
     * the context.
     */
    fun clear() {
        contextMap.remove()
    }
}
