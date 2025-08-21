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
package com.rubensgomes.msfmk.web.filter

import com.rubensgomes.msfmk.common.MDCConstants
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
