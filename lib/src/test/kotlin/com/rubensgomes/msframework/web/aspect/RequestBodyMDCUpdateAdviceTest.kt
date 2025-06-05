package com.rubensgomes.msframework.web.aspect

import com.rubensgomes.msframework.common.MDCConstants
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.springframework.core.MethodParameter
import org.springframework.http.HttpInputMessage
import org.springframework.http.converter.StringHttpMessageConverter
import java.lang.reflect.Type

/**
 * A simple data class to contain MDC related information.
 *
 * @author Rubens Gomes
 */
data class MDCData(
    val clientId: String,
    val transactionId: String,
)

/**
 * A simple data class whose properties do NOT match a valid MDC field
 *
 * @author Rubens Gomes
 */
data class NonMDCData(
    val something: String,
)

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

        assertEquals(
            data.clientId,
            MDC.get(MDCConstants.CLIENT_ID_KEY),
        )
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

        assertTrue(
            MDC.getMDCAdapter().copyOfContextMap.isNullOrEmpty(),
        )
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

        assertTrue(
            MDC.getMDCAdapter().copyOfContextMap.isNullOrEmpty(),
        )
    }
}
