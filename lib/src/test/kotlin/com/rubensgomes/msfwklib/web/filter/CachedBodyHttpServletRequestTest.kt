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

import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import java.io.ByteArrayInputStream
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@DisplayName("CachedBodyHttpServletRequest")
class CachedBodyHttpServletRequestTest {
    @Test
    fun `returns same body on multiple getInputStream calls`() {
        val body = "Hello, World!".toByteArray()
        val mockRequest = mock(HttpServletRequest::class.java)
        val inputStream =
            object : ServletInputStream() {
                private val delegate = ByteArrayInputStream(body)

                override fun read(): Int = delegate.read()

                override fun isFinished(): Boolean = delegate.available() == 0

                override fun isReady(): Boolean = true

                override fun setReadListener(readListener: jakarta.servlet.ReadListener?) {}
            }
        `when`(mockRequest.inputStream).thenReturn(inputStream)

        val cachedRequest = CachedBodyHttpServletRequest(mockRequest)
        val firstRead = cachedRequest.inputStream.readAllBytes()
        val secondRead = cachedRequest.inputStream.readAllBytes()

        assertArrayEquals(body, firstRead)
        assertArrayEquals(body, secondRead)
    }

    @Test
    fun `returns same body on multiple getReader calls`() {
        val body = "Test Body"
        val mockRequest = mock(HttpServletRequest::class.java)
        val inputStream =
            object : ServletInputStream() {
                private val delegate = ByteArrayInputStream(body.toByteArray())

                override fun read(): Int = delegate.read()

                override fun isFinished(): Boolean = delegate.available() == 0

                override fun isReady(): Boolean = true

                override fun setReadListener(readListener: jakarta.servlet.ReadListener?) {}
            }
        `when`(mockRequest.inputStream).thenReturn(inputStream)

        val cachedRequest = CachedBodyHttpServletRequest(mockRequest)
        val firstRead = cachedRequest.reader.readText()
        val secondRead = cachedRequest.reader.readText()

        assertEquals(body, firstRead)
        assertEquals(body, secondRead)
    }

    @Test
    fun `handles empty request body`() {
        val body = "".toByteArray()
        val mockRequest = mock(HttpServletRequest::class.java)
        val inputStream =
            object : ServletInputStream() {
                private val delegate = ByteArrayInputStream(body)

                override fun read(): Int = delegate.read()

                override fun isFinished(): Boolean = delegate.available() == 0

                override fun isReady(): Boolean = true

                override fun setReadListener(readListener: jakarta.servlet.ReadListener?) {}
            }
        `when`(mockRequest.inputStream).thenReturn(inputStream)

        val cachedRequest = CachedBodyHttpServletRequest(mockRequest)
        val readBytes = cachedRequest.inputStream.readAllBytes()
        val readText = cachedRequest.reader.readText()

        assertArrayEquals(body, readBytes)
        assertEquals("", readText)
    }

    @Test
    fun `isFinished returns true after reading all bytes`() {
        val body = "abc".toByteArray()
        val mockRequest = mock(HttpServletRequest::class.java)
        val inputStream =
            object : ServletInputStream() {
                private val delegate = ByteArrayInputStream(body)

                override fun read(): Int = delegate.read()

                override fun isFinished(): Boolean = delegate.available() == 0

                override fun isReady(): Boolean = true

                override fun setReadListener(readListener: jakarta.servlet.ReadListener?) {}
            }
        `when`(mockRequest.inputStream).thenReturn(inputStream)

        val cachedRequest = CachedBodyHttpServletRequest(mockRequest)
        val servletInputStream = cachedRequest.inputStream
        while (servletInputStream.read() != -1) {
            // consume all bytes
        }
        assertTrue(servletInputStream.isFinished)
    }
}
