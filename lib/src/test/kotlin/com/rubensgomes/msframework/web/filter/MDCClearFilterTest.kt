package com.rubensgomes.msframework.web.filter

import com.rubensgomes.msframework.common.MDCConstants
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class MDCClearFilterTest {
    // the following Spring framework mocked web objects are fundamental to
    // unit test our filter. Without these Spring web mocked instances, much
    // behaviour would need to be added to our corresponding mocked objects.
    private val request = MockHttpServletRequest("GET", "/api/dummy")
    private val response = MockHttpServletResponse()
    private val filterChain = MockFilterChain()

    @Test
    fun `ensure constructor works`() {
        val contextResetFilter = MDCClearFilter()
        assertNotNull(contextResetFilter)
    }

    @Test
    fun `ensure MDC filter is cleared using clientId`() {
        val clientId = "client-id"
        MDC.put(MDCConstants.CLIENT_ID_KEY, clientId)
        val filter = MDCClearFilter()
        assertEquals(clientId, MDC.get(MDCConstants.CLIENT_ID_KEY))

        filter.doFilter(request, response, filterChain)

        assertNull(MDC.get(MDCConstants.CLIENT_ID_KEY))
    }

    @Test
    fun `ensure MDC filter is cleared using transactionId`() {
        val transactionId = "transaction-id"
        MDC.put(MDCConstants.TRANSACTION_ID_KEY, transactionId)
        val filter = MDCClearFilter()
        assertEquals(transactionId, MDC.get(MDCConstants.TRANSACTION_ID_KEY))

        filter.doFilter(request, response, filterChain)

        assertNull(MDC.get(MDCConstants.TRANSACTION_ID_KEY))
    }
}
