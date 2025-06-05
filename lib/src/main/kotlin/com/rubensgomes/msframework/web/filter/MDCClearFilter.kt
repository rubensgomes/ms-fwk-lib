package com.rubensgomes.msframework.web.filter

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
