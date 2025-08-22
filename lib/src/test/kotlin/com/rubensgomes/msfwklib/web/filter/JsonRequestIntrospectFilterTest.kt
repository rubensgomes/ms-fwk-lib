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

import com.rubensgomes.msfwklib.threadlocal.ContextHolder
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletInputStream
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import java.io.ByteArrayInputStream
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.MediaType

@DisplayName("JsonRequestIntrospectFilter")
class JsonRequestIntrospectFilterTest {

    @BeforeEach
    fun setUp() {
        ContextHolder.clear()
    }

    @AfterEach
    fun tearDown() {
        ContextHolder.clear()
    }

    @Test
    fun `processes POST request with JSON content type and extracts interesting properties`() {
        val body = """{"clientId":"12345","transactionId":"abcde","otherProperty":"ignored"}"""
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
        `when`(mockRequest.method).thenReturn("POST")
        `when`(mockRequest.contentType).thenReturn(MediaType.APPLICATION_JSON_VALUE)

        val filter = JsonRequestIntrospectFilter()
        val mockChain = mock(FilterChain::class.java)

        filter.doFilter(mockRequest, mock(ServletResponse::class.java), mockChain)

        assertEquals("12345", ContextHolder.get("clientId"))
        assertEquals("abcde", ContextHolder.get("transactionId"))
    }

    @Test
    fun `skips non-POST requests`() {
        val mockRequest = mock(HttpServletRequest::class.java)
        `when`(mockRequest.method).thenReturn("GET")
        `when`(mockRequest.contentType).thenReturn(MediaType.APPLICATION_JSON_VALUE)

        val filter = JsonRequestIntrospectFilter()
        val mockChain = mock(FilterChain::class.java)

        filter.doFilter(mockRequest, mock(ServletResponse::class.java), mockChain)

        assertEquals(null, ContextHolder.get("clientId"))
        assertEquals(null, ContextHolder.get("transactionId"))
    }

    @Test
    fun `skips requests with non-JSON content type`() {
        val mockRequest = mock(HttpServletRequest::class.java)
        `when`(mockRequest.method).thenReturn("POST")
        `when`(mockRequest.contentType).thenReturn(MediaType.TEXT_PLAIN_VALUE)

        val filter = JsonRequestIntrospectFilter()
        val mockChain = mock(FilterChain::class.java)

        filter.doFilter(mockRequest, mock(ServletResponse::class.java), mockChain)

        assertEquals(null, ContextHolder.get("clientId"))
        assertEquals(null, ContextHolder.get("transactionId"))
    }

    @Test
    fun `handles empty JSON body gracefully`() {
        val body = "{}"
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
        `when`(mockRequest.method).thenReturn("POST")
        `when`(mockRequest.contentType).thenReturn(MediaType.APPLICATION_JSON_VALUE)

        val filter = JsonRequestIntrospectFilter()
        val mockChain = mock(FilterChain::class.java)

        filter.doFilter(mockRequest, mock(ServletResponse::class.java), mockChain)

        assertEquals(null, ContextHolder.get("clientId"))
        assertEquals(null, ContextHolder.get("transactionId"))
    }

    @Test
    fun `ignores properties not in the interestingProperties set`() {
        val body = """{"unrelatedProperty":"value"}"""
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
        `when`(mockRequest.method).thenReturn("POST")
        `when`(mockRequest.contentType).thenReturn(MediaType.APPLICATION_JSON_VALUE)

        val filter = JsonRequestIntrospectFilter()
        val mockChain = mock(FilterChain::class.java)

        filter.doFilter(mockRequest, mock(ServletResponse::class.java), mockChain)

        assertEquals(null, ContextHolder.get("clientId"))
        assertEquals(null, ContextHolder.get("transactionId"))
    }
}
