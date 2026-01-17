/*
 * Copyright 2026 Rubens Gomes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
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
package com.rubensgomes.msfwklib.common

/**
 * Constants for MDC (Mapped Diagnostic Context) key-value pairs used throughout the framework.
 *
 * This object contains predefined keys for storing contextual information in the SLF4J MDC, which
 * enables consistent logging across the application with request-scoped data.
 *
 * The MDC keys defined here are used by various framework components including:
 * - Request filters for populating context from HTTP headers or request body
 * - Aspect-oriented programming (AOP) advice for automatic MDC population
 * - Filters for clearing MDC context at the end of request processing
 *
 * Usage examples:
 * ```kotlin
 * // Setting values in MDC
 * MDC.put(MDCConstants.CLIENT_ID_KEY, "client123")
 * MDC.put(MDCConstants.TRANSACTION_ID_KEY, "txn456")
 *
 * // Retrieving values from MDC
 * val clientId = MDC.get(MDCConstants.CLIENT_ID_KEY)
 * val transactionId = MDC.get(MDCConstants.TRANSACTION_ID_KEY)
 *
 * // Iterating over all supported keys
 * MDCConstants.MDC_KEYS.forEach { key ->
 *     val value = MDC.get(key)
 *     if (!value.isNullOrBlank()) {
 *         println("$key: $value")
 *     }
 * }
 * ```
 *
 * @see org.slf4j.MDC
 * @author Rubens Gomes
 * @since 0.0.1
 */
object MDCConstants {
  /**
   * MDC key for storing client identifier information.
   *
   * This key is used to store a unique identifier for the client making the request. The client ID
   * can be extracted from HTTP headers, request body, or other sources and is automatically
   * populated by framework components.
   */
  const val CLIENT_ID_KEY = "clientId"

  /**
   * MDC key for storing transaction identifier information.
   *
   * This key is used to store a unique identifier for the current transaction or request. The
   * transaction ID helps correlate log entries across different components and services for the
   * same logical operation.
   */
  const val TRANSACTION_ID_KEY = "transactionId"

  /**
   * Immutable set containing all supported MDC keys.
   *
   * This collection can be used for:
   * - Iterating over all available MDC keys
   * - Validating whether a key is supported by the framework
   * - Bulk operations like clearing all framework-related MDC entries
   * - Configuration validation in components that work with MDC data
   *
   * The set is immutable to prevent accidental modification of the supported keys.
   */
  val MDC_KEYS = setOf(CLIENT_ID_KEY, TRANSACTION_ID_KEY)
}
