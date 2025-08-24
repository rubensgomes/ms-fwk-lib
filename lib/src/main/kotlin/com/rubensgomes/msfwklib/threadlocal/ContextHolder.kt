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
package com.rubensgomes.msfwklib.threadlocal

import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.commons.lang3.Validate

private val log = KotlinLogging.logger {}

/**
 * Thread-local context holder for managing request-scoped contextual information across application
 * layers.
 *
 * `ContextHolder` is a singleton object that provides a thread-safe mechanism for storing and
 * retrieving key-value pairs within the scope of a single thread. This is particularly useful in
 * web applications where you need to pass contextual information (such as request IDs, user
 * information, transaction IDs, or correlation IDs) across different layers of the application
 * without explicitly passing parameters through method calls.
 *
 * ## Key Features:
 * - **Thread Safety**: Each thread maintains its own isolated context map
 * - **Request Scoped**: Context is automatically isolated per HTTP request in web applications
 * - **Simple API**: Easy-to-use get/put/clear operations
 * - **Null Safety**: Handles null checks and empty contexts gracefully
 * - **Overwrite Protection**: Logs warnings when existing values are overwritten
 *
 * ## Thread Safety Considerations:
 * Since this uses [ThreadLocal], each thread has its own independent context map. This ensures:
 * - No interference between concurrent requests in web applications
 * - Thread-safe access without explicit synchronization
 * - Automatic cleanup when threads are recycled (in thread pools)
 *
 * ## Memory Management:
 * It's important to call [clear] at the end of request processing to prevent memory leaks,
 * especially in application servers that use thread pools where threads are reused.
 *
 * ## Usage Examples:
 * ```kotlin
 * // Setting context values at the beginning of request processing
 * ContextHolder.put("requestId", "req-12345")
 * ContextHolder.put("userId", "user-67890")
 * ContextHolder.put("correlationId", "corr-abcdef")
 *
 * // Retrieving context values anywhere in the application
 * val requestId = ContextHolder.get("requestId") // Returns "req-12345"
 * val userId = ContextHolder.get("userId")       // Returns "user-67890"
 * val missing = ContextHolder.get("missing")     // Returns null
 *
 * // Clearing context at the end of request (important for memory management)
 * ContextHolder.clear()
 * ```
 *
 * ## Common Use Cases:
 * - **Request Tracking**: Storing unique request identifiers for logging and debugging
 * - **User Context**: Maintaining user information throughout request processing
 * - **Correlation IDs**: Tracking requests across microservices and distributed systems
 * - **Security Context**: Storing authentication and authorization information
 * - **MDC Integration**: Populating SLF4J MDC (Mapped Diagnostic Context) for structured logging
 * - **Audit Trails**: Maintaining audit information for compliance and monitoring
 *
 * ## Integration with Web Frameworks:
 * This context holder is typically used with:
 * - Servlet filters to populate initial context values
 * - Spring interceptors for request preprocessing
 * - Aspect-oriented programming (AOP) for cross-cutting concerns
 * - Exception handlers for error context preservation
 *
 * @see ThreadLocal
 * @see org.slf4j.MDC
 * @author Rubens Gomes
 * @since 0.0.1
 */
object ContextHolder {
    // Thread-local storage for a mutable map of String key-value pairs
    private val contextMap = ThreadLocal<MutableMap<String, String>>()

    /**
     * Retrieves the value associated with the specified key from the current thread's context.
     *
     * This method provides safe access to thread-local context values with comprehensive null
     * checking and logging for debugging purposes. The operation is thread-safe and will only
     * access values stored by the current thread.
     *
     * ## Behavior:
     * - Returns `null` if the context is empty or uninitialized
     * - Returns `null` if the specified key is not found in the context
     * - Logs informational messages when context is empty or key is missing
     * - Logs debug information about successful retrievals
     *
     * ## Example Usage:
     * ```kotlin
     * // Retrieve a request ID that was set earlier in the request lifecycle
     * val requestId = ContextHolder.get("requestId")
     * if (requestId != null) {
     *     logger.info("Processing request: $requestId")
     * }
     *
     * // Safe retrieval with null check
     * val userId = ContextHolder.get("userId") ?: "anonymous"
     * ```
     *
     * @param key The key to look up in the thread-local context. Must not be blank or empty.
     * @return The value associated with the key, or `null` if the key is not found or the context
     *   is empty.
     * @throws IllegalArgumentException if the key is blank or empty.
     * @see put
     * @see clear
     */
    fun get(key: String): String? {
        Validate.notBlank(key, "Key must not be blank")
        log.debug { "Retrieving value for key: $key" }

        if (contextMap.get().isNullOrEmpty()) {
            log.info { "ThreadLocal context is empty" }
            return null
        }

        if (!contextMap.get().contains(key)) {
            log.info { "ThreadLocal does not contain the key: $key" }
            return null
        }

        val value = contextMap.get()[key]
        log.debug { "Retrieved value for key: $key = $value" }
        return value
    }

    /**
     * Stores a key-value pair in the current thread's context map.
     *
     * This method safely stores contextual information that can be retrieved later in the same
     * thread. If the context map doesn't exist for the current thread, it will be created
     * automatically. If the key already exists, its value will be overwritten and a warning will be
     * logged.
     *
     * ## Behavior:
     * - Creates a new context map if none exists for the current thread
     * - Overwrites existing values for the same key (with warning log)
     * - Thread-safe operation that only affects the current thread's context
     * - Logs debug information about the storage operation
     *
     * ## Best Practices:
     * - Use consistent key naming conventions across your application
     * - Consider using constants for commonly used keys to avoid typos
     * - Be mindful of overwriting existing values (warnings will be logged)
     * - Remember to call [clear] at the end of request processing
     *
     * ## Example Usage:
     * ```kotlin
     * // Set request-scoped information
     * ContextHolder.put("requestId", UUID.randomUUID().toString())
     * ContextHolder.put("userId", authentication.principal.name)
     * ContextHolder.put("startTime", System.currentTimeMillis().toString())
     *
     * // Using constants for key names (recommended)
     * object ContextKeys {
     *     const val REQUEST_ID = "requestId"
     *     const val USER_ID = "userId"
     * }
     * ContextHolder.put(ContextKeys.REQUEST_ID, "req-12345")
     * ```
     *
     * @param key The key to store in the context. Must not be blank or empty.
     * @param value The value to associate with the key. Can be any string value.
     * @throws IllegalArgumentException if the key is blank or empty.
     * @see get
     * @see clear
     */
    fun put(key: String, value: String) {
        Validate.notBlank(key, "Key must not be blank")
        log.debug { "Putting value for key: $key = $value " }
        val map = contextMap.get() ?: mutableMapOf()

        if (map.containsKey(key)) {
            log.warn { "Overwriting existing value for key: $key" }
        }

        map[key] = value
        contextMap.set(map)
    }

    /**
     * Clears all context data for the current thread.
     *
     * This method removes the entire context map for the current thread, effectively cleaning up
     * all stored key-value pairs. This is crucial for preventing memory leaks in environments where
     * threads are reused (such as application servers with thread pools).
     *
     * ## When to Call:
     * - **End of HTTP requests**: In web applications, call this in finally blocks or filters
     * - **End of background tasks**: When completing asynchronous operations
     * - **Exception handling**: Ensure cleanup even when errors occur
     * - **Thread pool environments**: Before returning threads to the pool
     *
     * ## Memory Management:
     * Failing to clear the context can lead to:
     * - Memory leaks in long-running applications
     * - Stale data being available to subsequent requests on reused threads
     * - Potential security issues if sensitive data is left in memory
     *
     * ## Integration Examples:
     * ```kotlin
     * // In a servlet filter
     * try {
     *     // Request processing
     *     ContextHolder.put("requestId", generateRequestId())
     *     // ... process request
     * } finally {
     *     ContextHolder.clear() // Always cleanup
     * }
     *
     * // In a Spring interceptor
     * override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any?, ex: Exception?) {
     *     ContextHolder.clear()
     * }
     *
     * // In async processing
     * CompletableFuture.runAsync {
     *     try {
     *         ContextHolder.put("taskId", taskId)
     *         // ... process task
     *     } finally {
     *         ContextHolder.clear()
     *     }
     * }
     * ```
     *
     * ## Performance Impact:
     * This operation is lightweight and should be called liberally to ensure proper cleanup. The
     * performance cost is minimal compared to the benefits of preventing memory leaks.
     *
     * @see get
     * @see put
     */
    fun clear() {
        contextMap.remove()
    }
}
