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
package com.rubensgomes.msfwklib.web.filter

import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

/**
 * CachedBodyHttpServletRequest is a wrapper for [HttpServletRequest] that caches the request body.
 *
 * This allows the request body to be read multiple times, which is not possible with the standard
 * [HttpServletRequest] as its input stream can only be read once. The body is cached as a byte
 * array upon instantiation and subsequent calls to [getInputStream] or [getReader] will read from
 * the cached data.
 *
 * @param request The original HTTP servlet request to wrap and cache.
 * @constructor Wraps the given [HttpServletRequest] and caches its body.
 * @author Rubens Gomes
 */
class CachedBodyHttpServletRequest(request: HttpServletRequest) :
    HttpServletRequestWrapper(request) {
    /** Cached copy of the request body as a byte array. */
    private val cachedBody: ByteArray = request.inputStream.readAllBytes()

    /** Returns a [ServletInputStream] that reads from the cached request body. */
    override fun getInputStream(): ServletInputStream {
        val byteArrayInputStream = ByteArrayInputStream(cachedBody)
        return object : ServletInputStream() {
            override fun read(): Int = byteArrayInputStream.read()

            override fun isFinished(): Boolean = byteArrayInputStream.available() == 0

            override fun isReady(): Boolean = true

            override fun setReadListener(readListener: ReadListener?) {}
        }
    }

    /** Returns a [BufferedReader] that reads from the cached request body. */
    override fun getReader(): BufferedReader = BufferedReader(InputStreamReader(this.inputStream))
}
