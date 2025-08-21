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
import com.rubensgomes.msfmk.threadlocal.ContextHolder
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest
import kotlin.reflect.full.memberProperties
import org.springframework.http.MediaType
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

/**
 * A servlet filter that introspects JSON request bodies and extracts specific properties to store
 * in thread-local context for logging and tracking purposes.
 *
 * This filter operates on POST requests with JSON content type (application/json) and extracts
 * predefined "interesting" properties from the request body. The extracted properties are stored in
 * [ContextHolder] for use throughout the request lifecycle.
 *
 * The filter wraps the original request in a [CachedBodyHttpServletRequest] to allow multiple reads
 * of the request body without consuming the input stream.
 *
 * @property objectMapper Jackson ObjectMapper instance for JSON processing
 * @property interestingProperties Set of property names to extract from requests ("clientId",
 *   "transactionId")
 * @see CachedBodyHttpServletRequest
 * @see ContextHolder
 * @author Rubens Gomes
 */
@Component
@WebFilter("/api/*")
class JsonRequestIntrospectFilter : Filter {

    /**
     * Filters incoming requests to extract and store interesting properties from JSON request
     * bodies.
     *
     * This method checks if the request is a POST request with JSON content type, and if so, wraps
     * it in a [CachedBodyHttpServletRequest] to allow multiple reads of the request body. It then
     * uses reflection to extract properties matching the [interestingProperties] set and stores
     * them in the [ContextHolder] for thread-local access.
     *
     * @param request the servlet request to be processed
     * @param response the servlet response to be returned
     * @param chain the filter chain to continue processing the request
     */
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        log.trace { "entering filter" }

        if (
            request is HttpServletRequest &&
                request.method == "POST" &&
                request.contentType?.contains(MediaType.APPLICATION_JSON_VALUE) == true
        ) {
            log.trace { "parsing request for fields: $MDCConstants.MDC_KEYS" }
            val wrappedRequest = CachedBodyHttpServletRequest(request)

            for (property in wrappedRequest::class.memberProperties) {
                val name = property.name
                val value = property.getter.call(wrappedRequest)

                // we need to be careful logging properties that might be sensitive in
                // production. We should not log all the properties found.

                if (name in MDCConstants.MDC_KEYS) {
                    log.info { "Storing property in ThreadLocal: $name = $value" }
                    ContextHolder.put(name, value.toString())
                }
            }

            chain.doFilter(wrappedRequest, response)
        } else {
            chain.doFilter(request, response)
        }
    }
}
