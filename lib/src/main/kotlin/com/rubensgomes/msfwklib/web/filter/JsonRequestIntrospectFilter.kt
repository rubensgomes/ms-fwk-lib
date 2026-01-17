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
package com.rubensgomes.msfwklib.web.filter

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.rubensgomes.msfwklib.common.MDCConstants
import com.rubensgomes.msfwklib.threadlocal.ContextHolder
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest
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
 * @since 0.0.1
 */
@Component
@WebFilter("/api/*")
class JsonRequestIntrospectFilter : Filter {

  private val objectMapper = ObjectMapper()

  /**
   * Filters incoming requests to extract and store interesting properties from JSON request bodies.
   *
   * This method checks if the request is a POST request with JSON content type, and if so, wraps it
   * in a [CachedBodyHttpServletRequest] to allow multiple reads of the request body. It then parses
   * the JSON content to extract properties matching the [MDCConstants.MDC_KEYS] set and stores them
   * in the [ContextHolder] for thread-local access.
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
      log.trace { "parsing request for fields: ${MDCConstants.MDC_KEYS}" }
      val wrappedRequest = CachedBodyHttpServletRequest(request)

      try {
        // Read the JSON content from the request body
        val inputStream = wrappedRequest.inputStream
        val jsonContent = inputStream.readAllBytes()

        if (jsonContent.isNotEmpty()) {
          val jsonNode: JsonNode = objectMapper.readTree(jsonContent)

          // Extract interesting properties from the JSON
          for (key in MDCConstants.MDC_KEYS) {
            val jsonValue = jsonNode.get(key)
            if (jsonValue != null && !jsonValue.isNull) {
              val value = jsonValue.asText()
              log.info { "Storing property in ThreadLocal: $key = $value" }
              ContextHolder.put(key, value)
            }
          }
        }
      } catch (e: Exception) {
        log.warn(e) { "Failed to parse JSON request body" }
      }

      chain.doFilter(wrappedRequest, response)
    } else {
      chain.doFilter(request, response)
    }
  }
}
