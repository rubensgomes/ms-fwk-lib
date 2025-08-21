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
package com.rubensgomes.msfwklib.web.aspect

import com.rubensgomes.msfwklib.common.MDCConstants
import io.mockk.mockk
import java.lang.reflect.Type
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.springframework.core.MethodParameter
import org.springframework.http.HttpInputMessage
import org.springframework.http.converter.StringHttpMessageConverter

/**
 * A simple data class to contain MDC related information.
 *
 * @author Rubens Gomes
 */
data class MDCData(val clientId: String, val transactionId: String)

/**
 * A simple data class whose properties do NOT match a valid MDC field
 *
 * @author Rubens Gomes
 */
data class NonMDCData(val something: String)

class RequestBodyMDCUpdateAdviceTest {
    private val advice = RequestBodyMDCUpdateAdvice()

    @BeforeEach
    fun setUp() {
        MDC.clear()
    }

    @Test
    fun `ensure MDC is updated using MDCData`() {
        val data = MDCData("123", "")
        val inputMessage = mockk<HttpInputMessage>()
        val parameter = mockk<MethodParameter>()
        val targetType = mockk<Type>()

        advice.afterBodyRead(
            data,
            inputMessage,
            parameter,
            targetType,
            StringHttpMessageConverter::class.java,
        )

        assertEquals(data.clientId, MDC.get(MDCConstants.CLIENT_ID_KEY))
    }

    @Test
    fun `ensure MDC is NOT updated when MDC fields found are blnak`() {
        val data = MDCData(" ", " ")
        val inputMessage = mockk<HttpInputMessage>()
        val parameter = mockk<MethodParameter>()
        val targetType = mockk<Type>()

        advice.afterBodyRead(
            data,
            inputMessage,
            parameter,
            targetType,
            StringHttpMessageConverter::class.java,
        )

        assertTrue(MDC.getMDCAdapter().copyOfContextMap.isNullOrEmpty())
    }

    @Test
    fun `ensure MDC is NOT updated when MDC fields not found in object`() {
        val data = NonMDCData("hello")
        val inputMessage = mockk<HttpInputMessage>()
        val parameter = mockk<MethodParameter>()
        val targetType = mockk<Type>()

        advice.afterBodyRead(
            data,
            inputMessage,
            parameter,
            targetType,
            StringHttpMessageConverter::class.java,
        )

        assertTrue(MDC.getMDCAdapter().copyOfContextMap.isNullOrEmpty())
    }
}
