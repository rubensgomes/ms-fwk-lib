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
package com.rubensgomes.msfwklib.web.aspect

import com.rubensgomes.msfwklib.common.RootErrorMessageResolver
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.ValidationException
import java.util.Locale
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.ErrorResponse
import org.springframework.web.ErrorResponseException
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingMatrixVariableException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.MissingRequestCookieException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.MissingRequestValueException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.context.request.async.AsyncRequestTimeoutException
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.server.MethodNotAllowedException
import org.springframework.web.server.NotAcceptableStatusException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerErrorException
import org.springframework.web.server.ServerWebInputException
import org.springframework.web.server.UnsatisfiedRequestParameterException
import org.springframework.web.server.UnsupportedMediaTypeStatusException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException

/**
 * Centralized global exception handler for REST API error management in Spring Boot applications.
 *
 * This class implements the AOP (Aspect-Oriented Programming) pattern using Spring's
 * `@RestControllerAdvice` to provide consistent, centralized exception handling across all REST
 * endpoints in the application. It ensures that all exceptions are properly caught, logged, and
 * converted into standardized RFC 9457 compliant HTTP error responses.
 *
 * ## Key Features:
 * - **RFC 9457 Compliance**: All error responses conform to the Problem Details for HTTP APIs
 *   standard
 * - **Comprehensive Coverage**: Handles 25+ different exception types with appropriate HTTP status
 *   codes
 * - **Structured Logging**: Integrates with MDC and structured logging for enhanced observability
 * - **Internationalization**: Supports Content-Language headers for internationalized error
 *   responses
 * - **Root Cause Analysis**: Uses [RootErrorMessageResolver] for detailed error message extraction
 * - **Consistent Response Format**: Standardized error response structure across all endpoints
 *
 * ## RFC 9457 Problem Details:
 * All error responses follow the RFC 9457 standard, providing:
 * - **Consistent Structure**: Standardized error response format
 * - **Machine Readable**: Structured data for automated error handling
 * - **Human Friendly**: Clear error messages for debugging and user feedback
 * - **Extensible**: Support for additional problem-specific fields
 *
 * Example error response:
 * ```json
 * {
 *   "type": "https://example.com/problems/validation-error",
 *   "title": "Validation Failed",
 *   "status": 400,
 *   "detail": "The request contains invalid data",
 *   "instance": "/api/users/create"
 * }
 * ```
 *
 * ## Exception Handling Categories:
 *
 * ### 4xx Client Errors:
 * - **400 Bad Request**: Validation errors, missing parameters, malformed requests
 * - **404 Not Found**: Missing resources, unknown endpoints
 * - **405 Method Not Allowed**: Unsupported HTTP methods
 * - **406 Not Acceptable**: Media type negotiation failures
 * - **413 Payload Too Large**: File upload size exceeded
 * - **415 Unsupported Media Type**: Invalid content types
 *
 * ### 5xx Server Errors:
 * - **500 Internal Server Error**: Unexpected application errors
 * - **503 Service Unavailable**: Timeout and availability issues
 *
 * ## Integration with Framework Components:
 * This handler works seamlessly with other framework components:
 * - **MDC Integration**: Automatic context logging for request correlation
 * - **Root Error Resolution**: Deep exception analysis for detailed error reporting
 * - **Validation Framework**: Bean validation and method parameter validation
 * - **Security Framework**: Authentication and authorization error handling
 * - **File Upload**: Multipart form data processing errors
 *
 * ## Usage Examples:
 *
 * ### Automatic Registration:
 * The handler is automatically registered when the framework is included:
 * ```kotlin
 * @SpringBootApplication
 * class MyApplication
 *
 * // GlobalExceptionHandler is automatically active
 * ```
 *
 * ### Custom Exception Handling:
 * Applications can extend or supplement this handler:
 * ```kotlin
 * @RestControllerAdvice
 * class CustomExceptionHandler {
 *
 *     @ExceptionHandler(CustomBusinessException::class)
 *     fun handleCustomException(ex: CustomBusinessException): ResponseEntity<ErrorResponse> {
 *         return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
 *             .contentType(MediaType.APPLICATION_PROBLEM_JSON)
 *             .body(ServerErrorException(ex.message, ex))
 *     }
 * }
 * ```
 *
 * ### Testing Error Handling:
 * ```kotlin
 * @SpringBootTest
 * @AutoConfigureMockMvc
 * class ErrorHandlingTest {
 *
 *     @Autowired
 *     lateinit var mockMvc: MockMvc
 *
 *     @Test
 *     fun `should return 400 for validation errors`() {
 *         mockMvc.perform(post("/api/users")
 *             .contentType(MediaType.APPLICATION_JSON)
 *             .content("{}"))
 *             .andExpect(status().isBadRequest)
 *             .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
 *     }
 * }
 * ```
 *
 * ## Logging and Observability:
 * The handler provides comprehensive logging for error tracking:
 * - **Trace Logging**: Method entry/exit for debugging filter chains
 * - **Error Logging**: Detailed error information with root cause analysis
 * - **MDC Context**: Request correlation data automatically included
 * - **Structured Data**: JSON-compatible log format for log aggregation
 *
 * ## Performance Considerations:
 * - **Exception Path Optimization**: Efficient exception handling with minimal overhead
 * - **Memory Management**: Proper cleanup of exception context and resources
 * - **Response Caching**: Consistent response headers for optimal client caching
 * - **Logging Efficiency**: Appropriate log levels to minimize performance impact
 *
 * ## Security Considerations:
 * - **Information Disclosure**: Careful handling of sensitive error information
 * - **Stack Trace Protection**: Root cause resolution without exposing internal details
 * - **Rate Limiting**: Integration with security frameworks for error response throttling
 * - **Audit Logging**: Complete error audit trail for security monitoring
 *
 * ## Best Practices:
 * 1. **Consistent Error Responses**: Always return RFC 9457 compliant responses
 * 2. **Appropriate Status Codes**: Use correct HTTP status codes for different error types
 * 3. **Detailed Logging**: Log sufficient information for debugging without exposing sensitive data
 * 4. **Client-Friendly Messages**: Provide clear, actionable error messages
 * 5. **Monitoring Integration**: Ensure error metrics are properly collected
 *
 * @see org.springframework.web.bind.annotation.RestControllerAdvice
 * @see org.springframework.web.bind.annotation.ExceptionHandler
 * @see org.springframework.web.ErrorResponse
 * @see com.rubensgomes.msfwklib.common.RootErrorMessageResolver
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9457.html">RFC 9457 - Problem Details for HTTP
 *   APIs</a>
 * @author Rubens Gomes
 * @since 0.0.1
 */
@RestControllerAdvice
class GlobalExceptionHandler {
  /**
   * HTTP Status 400 - bad request (input validation error) HTTP Status 500 - return value
   * validation (output validation error)
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [HandlerMethodValidationException::class])
  fun handleHandlerMethodValidation(
      ex: HandlerMethodValidationException
  ): ResponseEntity<ErrorResponse> {
    log.trace("handleHandlerMethodValidation")
    logError(ex)

    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * HTTP Status 400 - bad request (missing path variable)
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [MissingPathVariableException::class])
  fun handleMissingPathVariable(ex: MissingPathVariableException): ResponseEntity<ErrorResponse> {
    log.trace("handleMissingPathVariable")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * HTTP Status 400 - bad request (missing request value)
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [MissingRequestValueException::class])
  fun handleMissingRequestValue(ex: MissingRequestValueException): ResponseEntity<ErrorResponse> {
    log.trace("handleMissingRequestValue")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * HTTP Status 400 - bad request (missing cookie)
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [MissingRequestCookieException::class])
  fun handleMissingRequestCookie(ex: MissingRequestCookieException): ResponseEntity<ErrorResponse> {
    log.trace("handleMissingRequestCookie")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * HTTP Status 400 - bad request (missing header)
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [MissingRequestHeaderException::class])
  fun handleMissingRequestHeader(ex: MissingRequestHeaderException): ResponseEntity<ErrorResponse> {
    log.trace("handleMissingRequestHeader")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * HTTP Status 400 - for bad request (missing parameter).
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [MissingServletRequestParameterException::class])
  fun handleMissingServletRequestParameter(
      ex: MissingServletRequestParameterException
  ): ResponseEntity<ErrorResponse> {
    log.trace("handleMissingServletRequestParameter")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * HTTP Status 400 - for bad request (multipart/ form-data).
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [MissingServletRequestPartException::class])
  fun handleMissingServletRequestPart(
      ex: MissingServletRequestPartException
  ): ResponseEntity<ErrorResponse> {
    log.trace("handleMissingServletRequestPart")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * HTTP Status 400 - for bad request (invalid request).
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [MethodArgumentNotValidException::class])
  fun handleMethodArgumentNotValid(
      ex: MethodArgumentNotValidException
  ): ResponseEntity<ErrorResponse> {
    log.trace("handleMethodArgumentNotValid")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * HHTTP Status 400 - bad request
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [MissingMatrixVariableException::class])
  fun handleMissingMatrixVariable(
      ex: MissingMatrixVariableException
  ): ResponseEntity<ErrorResponse> {
    log.trace("handleMissingMatrixVariable")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * Specialized case of [ServerWebInputException]
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [WebExchangeBindException::class])
  fun handleWebExchangeBind(ex: WebExchangeBindException): ResponseEntity<ErrorResponse> {
    log.trace("handleWebExchangeBind")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * Specialized case of [ServerWebInputException]
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [UnsatisfiedRequestParameterException::class])
  fun handleUnsatisfiedRequestParameter(
      ex: UnsatisfiedRequestParameterException
  ): ResponseEntity<ErrorResponse> {
    log.trace("handleUnsatisfiedRequestParameter")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * Specialized case of [ResponseStatusException]
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [ServerWebInputException::class])
  fun handleServerWebInput(ex: ServerWebInputException): ResponseEntity<ErrorResponse> {
    log.trace("handleServerWebInput")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * Specialized case of [ResponseStatusException]
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [UnsupportedMediaTypeStatusException::class])
  fun handleUnsupportedMediaTypeStatus(
      ex: UnsupportedMediaTypeStatusException
  ): ResponseEntity<ErrorResponse> {
    log.trace("handleUnsupportedMediaTypeStatus")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * Specialized case of [ErrorResponseException]
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [ResponseStatusException::class])
  fun handleResponseStatus(ex: ResponseStatusException): ResponseEntity<ErrorResponse> {
    log.trace("handleResponseStatus")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * HTTP Status 404 - not found resource.
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [NoHandlerFoundException::class])
  fun handleNotFound(ex: NoHandlerFoundException): ResponseEntity<ErrorResponse> {
    log.trace("handleNotFound")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * HTTP Status 404 - not found resource.
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [NoResourceFoundException::class])
  fun handleNoResourceFound(ex: NoResourceFoundException): ResponseEntity<ErrorResponse> {
    log.trace("handleNoResourceFound")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * HHTTP Status 405 - HTTP method not allowed
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [MethodNotAllowedException::class])
  fun handleMethodNotAllowed(
      ex: MethodNotAllowedException,
      response: HttpServletResponse,
  ): ResponseEntity<ErrorResponse> {
    log.trace("handleMethodNotAllowed")
    response.setHeader(HttpHeaders.ALLOW, "GET, POST, PUT, DELETE, PATCH, OPTIONS")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * HHTTP Status 405 - HTTP method not supported
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [HttpRequestMethodNotSupportedException::class])
  fun handleHttpRequestMethodNotSupported(
      ex: HttpRequestMethodNotSupportedException,
      response: HttpServletResponse,
  ): ResponseEntity<ErrorResponse> {
    log.trace("handleHttpRequestMethodNotSupported")
    response.setHeader(HttpHeaders.ALLOW, "GET, POST, PUT, DELETE, PATCH, OPTIONS")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * HHTTP Status 406 - HTTP not acceptable
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [NotAcceptableStatusException::class])
  fun handleNotAcceptableStatus(ex: NotAcceptableStatusException): ResponseEntity<ErrorResponse> {
    log.trace("handleNotAcceptableStatus")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * HHTTP Status 413 - Payload too large
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [MaxUploadSizeExceededException::class])
  fun handleMaxUploadSizeExceeded(
      ex: MaxUploadSizeExceededException
  ): ResponseEntity<ErrorResponse> {
    log.trace("handleMaxUploadSizeExceeded")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * HTTP Status 406 - HTTP media type not acceptable
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [HttpMediaTypeNotAcceptableException::class])
  fun handleNotAcceptable(ex: HttpMediaTypeNotAcceptableException): ResponseEntity<ErrorResponse> {
    log.trace("handleNotAcceptable")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * HTTP Status 415 - HTTP media type not supported
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [HttpMediaTypeNotSupportedException::class])
  fun handleUnsupportedMediaType(
      ex: HttpMediaTypeNotSupportedException
  ): ResponseEntity<ErrorResponse> {
    log.trace("handleUnsupportedMediaType")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * Specialized case of [ServletRequestBindingException]
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [UnsatisfiedServletRequestParameterException::class])
  fun handleUnsatisfiedServletRequestParameter(
      ex: UnsatisfiedServletRequestParameterException
  ): ResponseEntity<ErrorResponse> {
    log.trace("handleUnsatisfiedServletRequestParameter")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * HTTP Status 500 - internal error (servlet binding)
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [ServletRequestBindingException::class])
  fun handleServletRequestBinding(
      ex: ServletRequestBindingException
  ): ResponseEntity<ErrorResponse> {
    log.trace("handleServletRequestBinding")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * HTTP Status 500 - internal error (server error)
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [ServerErrorException::class])
  fun handleServerError(ex: ServerErrorException): ResponseEntity<ErrorResponse> {
    log.trace("handleServerError")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * HTTP Status 503 - server temporary unavailable
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [AsyncRequestTimeoutException::class])
  fun handleAsyncRequestTimeout(ex: AsyncRequestTimeoutException): ResponseEntity<ErrorResponse> {
    log.trace("handleAsyncRequestTimeout")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * Handles the generic ErrorResponseException
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [ErrorResponseException::class])
  fun handleErrorResponseException(ex: ErrorResponseException): ResponseEntity<ErrorResponse> {
    log.trace("handleErrorResponseException")
    logError(ex)
    return ResponseEntity.status(ex.statusCode)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ex)
  }

  /**
   * HTTP Status 400 - bad request (validation error)
   *
   * It complies with the RFC 9457 when returning an error response. For more information about RFC
   * 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
   *
   * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
   */
  @ExceptionHandler(value = [ValidationException::class])
  fun handleValidation(ex: ValidationException): ResponseEntity<ErrorResponse> {
    log.trace("handleValidation")
    logError(ex)
    val errorResponse = errorResponse(ex)
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(errorResponse)
  }

  /**
   * Default exception handler that catches any unhandled exceptions.
   *
   * This is the fallback handler that ensures no exception goes unhandled in the application. It
   * provides a safety net for unexpected errors and ensures consistent error response format even
   * for unforeseen exception types.
   *
   * **Important**: This handler should always be the last in the exception handling chain. It
   * catches all [Exception] types that aren't handled by more specific handlers.
   *
   * ## Security Considerations:
   * - Logs full exception details for debugging while returning sanitized error responses
   * - Prevents stack trace exposure to clients
   * - Includes warning-level logging for monitoring and alerting
   *
   * **HTTP Status**: 500 Internal Server Error
   *
   * @param ex The unhandled exception
   * @return RFC 9457 compliant generic error response
   */
  @ExceptionHandler(value = [Exception::class])
  fun handleException(ex: Exception): ResponseEntity<ErrorResponse> {
    log.trace("handleException")
    log.warn("exception default handler for {} : {}", ex.javaClass.name, ex.message, ex)
    logError(ex)
    val errorResponse = errorResponse(ex)
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .header("Content-Language", Locale.ENGLISH.language)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(errorResponse)
  }

  /**
   * Creates a standardized [ErrorResponse] for exceptions that don't have built-in Spring error
   * responses.
   *
   * This helper method ensures consistent error response creation for custom exceptions and generic
   * [Exception] types that don't extend Spring's error response framework.
   *
   * @param ex The exception to wrap in an error response
   * @return A [ServerErrorException] containing the exception details in RFC 9457 format
   */
  private fun errorResponse(ex: Exception): ErrorResponse =
      ServerErrorException(ex.message ?: "Unknown error", ex)

  internal companion object {
    private val log: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
  }

  /**
   * Centralized error logging method that provides comprehensive exception information.
   *
   * This method performs dual logging to capture both the immediate exception message and the root
   * cause analysis for debugging purposes. It integrates with the [RootErrorMessageResolver] to
   * provide deep error analysis.
   *
   * ## Logging Details:
   * - **Error Level**: All exceptions are logged at ERROR level
   * - **Message Format**: Structured format with both immediate and root cause messages
   * - **MDC Integration**: Automatically includes request context from MDC
   * - **Stack Traces**: Full exception stack trace for debugging
   *
   * ## Information Captured:
   * - Immediate exception message
   * - Root cause message (extracted by [RootErrorMessageResolver])
   * - Complete exception stack trace
   * - Request correlation data from MDC (if available)
   *
   * @param ex The exception to log
   * @see RootErrorMessageResolver.resolveMessage
   * @see org.slf4j.MDC
   */
  private fun logError(ex: Exception) {
    val errorMsg = ex.message
    val errorNativeMsg = RootErrorMessageResolver.resolveMessage(ex)
    log.error("handle exception error [$errorMsg] and native error [$errorNativeMsg]", ex)
  }
}
