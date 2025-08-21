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

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.web.filter.OncePerRequestFilter

/**
 * This filter is responsible for clearing the [MDC] context at the entrace and exit points of the
 * request/response filter chain.
 *
 * @author Rubens Gomes
 */
class MDCClearFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        log.trace("Entering filter.")
        MDC.clear()

        filterChain.doFilter(request, response)

        MDC.clear()
        log.trace("Exiting filter.")
    }

    internal companion object {
        private val log: Logger = LoggerFactory.getLogger(MDCClearFilter::class.java)
    }
}
