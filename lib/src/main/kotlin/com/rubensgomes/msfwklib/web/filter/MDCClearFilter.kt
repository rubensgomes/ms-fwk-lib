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

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.web.filter.OncePerRequestFilter

private val log = KotlinLogging.logger {}

/**
 * Servlet filter that ensures proper cleanup of MDC (Mapped Diagnostic Context) at request
 * boundaries.
 *
 * This filter is critical for preventing memory leaks and ensuring thread safety in web
 * applications by automatically clearing MDC context at both the entrance and exit points of the
 * HTTP request processing lifecycle. It extends [OncePerRequestFilter] to guarantee execution only
 * once per request, even in complex filter chain scenarios with internal forwards or includes.
 *
 * ## Purpose and Importance:
 * The MDC (Mapped Diagnostic Context) is a thread-local storage mechanism provided by SLF4J that
 * allows applications to store contextual information (like request IDs, user IDs, correlation IDs)
 * that gets automatically included in log messages. However, in web applications running on
 * application servers with thread pools, threads are reused across requests, which can lead to:
 * 1. **Memory Leaks**: Stale MDC data accumulating over time
 * 2. **Data Leakage**: Sensitive information from one request appearing in logs of subsequent
 *    requests
 * 3. **Security Issues**: Cross-request data contamination
 * 4. **Debugging Confusion**: Incorrect contextual information in logs
 *
 * ## Filter Behavior:
 * - **Pre-Request Cleanup**: Clears any existing MDC data before request processing begins
 * - **Post-Request Cleanup**: Ensures MDC is cleared after request processing completes
 * - **Exception Safety**: MDC cleanup occurs even if exceptions are thrown during request
 *   processing
 * - **Thread Safety**: Each thread's MDC is handled independently without affecting other threads
 *
 * ## Integration with Logging Framework:
 * This filter works seamlessly with structured logging patterns:
 * ```kotlin
 * // During request processing, other components may populate MDC:
 * MDC.put("requestId", "req-12345")
 * MDC.put("userId", "user-67890")
 * MDC.put("correlationId", "corr-abcdef")
 *
 * // All subsequent log statements automatically include this context:
 * logger.info("Processing user request")
 * // Output: [req-12345] [user-67890] [corr-abcdef] Processing user request
 *
 * // This filter ensures cleanup happens automatically at request end
 * ```
 *
 * ## Execution Order:
 * This filter should be registered with high precedence (e.g., `Ordered.HIGHEST_PRECEDENCE`) to
 * ensure:
 * - MDC is cleared before any other filters that might populate it
 * - MDC cleanup happens after all other request processing is complete
 * - Consistent behavior regardless of other filters in the chain
 *
 * ## Monitoring and Observability:
 * The filter provides trace-level logging for debugging filter execution:
 * - Entry point logging: "Entering filter."
 * - Exit point logging: "Exiting filter."
 * - Useful for troubleshooting filter chain issues
 *
 * @see org.slf4j.MDC
 * @see org.springframework.web.filter.OncePerRequestFilter
 * @see jakarta.servlet.Filter
 * @author Rubens Gomes
 * @since 0.0.1
 */
class MDCClearFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        log.trace { "Entering filter." }
        MDC.clear()

        filterChain.doFilter(request, response)

        MDC.clear()
        log.trace { "Exiting filter." }
    }
}
