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
package com.rubensgomes.msfwklib.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.HttpMethod

private val log = KotlinLogging.logger {}

/**
 * Spring Boot configuration properties for web-related settings in the microservice framework.
 *
 * This configuration class manages HTTP method restrictions and web security settings through
 * externalized configuration. It provides type-safe configuration binding for web-related
 * properties defined in `application.yml` or `application.properties` files.
 *
 * ## Configuration Structure:
 * The properties are bound from the `app.web` prefix in your Spring configuration:
 * ```yaml
 * app:
 *   web:
 *     allowed-methods:
 *       - GET
 *       - POST
 *       - PUT
 *       - DELETE
 *       - PATCH
 *       - OPTIONS
 * ```
 *
 * ## Key Features:
 * - **Type Safety**: Validates HTTP methods at application startup
 * - **Default Values**: Provides sensible defaults (GET only) for security
 * - **Fail-Fast**: Throws exceptions during startup if configuration is invalid
 * - **Externalized Configuration**: Allows environment-specific method restrictions
 * - **Security by Default**: Restricts to GET method unless explicitly configured
 *
 * ## Validation Rules:
 * 1. **Non-empty requirement**: At least one HTTP method must be specified
 * 2. **Valid HTTP methods**: Only standard HTTP methods are accepted
 * 3. **Case sensitivity**: Method names must match Spring's `HttpMethod` enum exactly
 *
 * ## Usage Examples:
 *
 * ### Basic Configuration:
 * ```yaml
 * # application.yml
 * app:
 *   web:
 *     allowed-methods:
 *       - GET
 *       - POST
 * ```
 *
 * ### Development Environment (permissive):
 * ```yaml
 * # application-dev.yml
 * app:
 *   web:
 *     allowed-methods:
 *       - GET
 *       - POST
 *       - PUT
 *       - DELETE
 *       - PATCH
 *       - OPTIONS
 *       - HEAD
 * ```
 *
 * ### Production Environment (restrictive):
 * ```yaml
 * # application-prod.yml
 * app:
 *   web:
 *     allowed-methods:
 *       - GET
 *       - POST
 * ```
 *
 * ### Spring Boot Application Setup:
 * ```kotlin
 * @SpringBootApplication
 * @EnableConfigurationProperties(value = [WebProperties::class])
 * class MyApplication
 *
 * fun main(args: Array<String>) {
 *     runApplication<MyApplication>(*args)
 * }
 * ```
 *
 * ### Accessing Configuration in Components:
 * ```kotlin
 * @Component
 * class WebSecurityConfig(private val webProperties: WebProperties) {
 *
 *     fun configureAllowedMethods(): Set<HttpMethod> {
 *         return webProperties.allowedMethods
 *             .map { HttpMethod.valueOf(it) }
 *             .toSet()
 *     }
 * }
 * ```
 *
 * ## Integration with Framework Components:
 * This configuration is typically used by:
 * - **CORS Configuration**: Setting allowed methods for cross-origin requests
 * - **Security Filters**: Restricting HTTP methods at the filter level
 * - **Method Validation**: Ensuring only permitted methods are processed
 * - **API Documentation**: Generating OpenAPI specs with correct method restrictions
 * - **Load Balancer Configuration**: Health check and routing method specifications
 *
 * ## Error Handling:
 * The class performs validation during Spring context initialization:
 * - Throws `IllegalArgumentException` for empty method lists
 * - Throws `IllegalArgumentException` for invalid HTTP method names
 * - Logs detailed error messages for troubleshooting
 *
 * ## Security Considerations:
 * - **Principle of Least Privilege**: Default configuration only allows GET methods
 * - **Environment-Specific**: Different environments can have different restrictions
 * - **Fail-Safe**: Invalid configuration prevents application startup
 * - **Audit Trail**: All validation decisions are logged for security auditing
 *
 * @property allowedMethods List of HTTP method names that are permitted by the application. Must
 *   contain valid HTTP method names as defined by [HttpMethod]. Defaults to `["GET"]` for security
 *   if not specified.
 * @see org.springframework.boot.context.properties.ConfigurationProperties
 * @see org.springframework.http.HttpMethod
 * @see org.springframework.boot.context.properties.EnableConfigurationProperties
 * @author Rubens Gomes
 * @since 0.0.1
 */
@ConfigurationProperties(prefix = "app.web")
data class WebProperties(
    /**
     * List of HTTP method names that are allowed by the application.
     *
     * This property defines which HTTP methods the application will accept and process. Each method
     * name must be a valid HTTP method as defined by Spring's [HttpMethod] enum.
     *
     * ## Validation:
     * - Must not be empty (at least one method required)
     * - All method names must be valid HTTP methods
     * - Method names are case-sensitive and must match [HttpMethod] enum values exactly
     *
     * ## Examples:
     * Valid method names: `GET`, `POST`, `PUT`, `DELETE`, `PATCH`, `OPTIONS`, `HEAD`, `TRACE`
     *
     * ## Default Value:
     * If not specified in configuration, defaults to `["GET"]` for security reasons. This ensures
     * that applications start with minimal permissions and require explicit configuration to enable
     * more permissive HTTP methods.
     *
     * @see HttpMethod
     */
    val allowedMethods: List<String> = listOf(HttpMethod.GET.name())
) {
  /**
   * Initializer block that validates the configuration at application startup.
   *
   * This validation ensures that the application fails fast with clear error messages if the
   * configuration is invalid, rather than failing at runtime when requests are processed.
   *
   * ## Validation Steps:
   * 1. **Empty List Check**: Ensures at least one HTTP method is configured
   * 2. **Method Validation**: Verifies each method name is a valid HTTP method
   * 3. **Logging**: Records validation results for debugging and auditing
   *
   * ## Error Conditions:
   * - Throws [IllegalArgumentException] if `allowedMethods` is empty
   * - Throws [IllegalArgumentException] if any method name is not a valid HTTP method
   *
   * ## Logging Behavior:
   * - Logs error messages for invalid configurations
   * - Logs debug messages for each valid method (useful for troubleshooting)
   *
   * @throws IllegalArgumentException if the configuration is invalid
   */
  init {
    // run checks to ensure the "app.web.allowed-methods" is valid
    if (allowedMethods.isEmpty()) {
      val msg = "Spring configuration property [app.web.allowed-methods] must not be empty"
      log.error { msg }
      throw IllegalArgumentException(msg)
    }

    for (method in allowedMethods) {
      val httpMethod = HttpMethod.valueOf(method)
      when (httpMethod) {
        in HttpMethod.values() -> log.debug { "HTTP method [$method] is valid" }
        else -> {
          log.error { "HTTP method [$method] is not valid" }
          throw IllegalArgumentException("HTTP method [$method] is not valid")
        }
      }
    }
  }
}
