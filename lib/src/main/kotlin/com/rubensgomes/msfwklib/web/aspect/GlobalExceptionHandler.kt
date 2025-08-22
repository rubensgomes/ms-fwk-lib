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
package com.rubensgomes.msfwklib.web.aspect

import com.rubensgomes.msfwklib.common.RootCauseErrorMessageI
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
 * A global exception handler that implements an AOP (Aspect Oriented Programming) advice code to be
 * applied to every web request. It uses an [ExceptionHandler] to catch and handle exceptions and
 * return a [ResponseEntity] with [ErrorResponse] based on the RFC 9475.
 *
 * For more information about RFC 9457 syntax, see
 * [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html)
 *
 * @author Rubens Gomes
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    /**
     * HTTP Status 400 - bad request (input validation error) HTTP Status 500 - return value
     * validation (output validation error)
     *
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
     *
     * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
     */
    @ExceptionHandler(value = [MissingRequestCookieException::class])
    fun handleMissingRequestCookie(
        ex: MissingRequestCookieException
    ): ResponseEntity<ErrorResponse> {
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
     *
     * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
     */
    @ExceptionHandler(value = [MissingRequestHeaderException::class])
    fun handleMissingRequestHeader(
        ex: MissingRequestHeaderException
    ): ResponseEntity<ErrorResponse> {
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
     *
     * @return [ResponseEntity] with an RFC 9457 [ErrorResponse] in the body.
     */
    @ExceptionHandler(value = [HttpMediaTypeNotAcceptableException::class])
    fun handleNotAcceptable(
        ex: HttpMediaTypeNotAcceptableException
    ): ResponseEntity<ErrorResponse> {
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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
     * It complies with the RFC 9457 when returning an error response. For more information about
     * RFC 9457 syntax, see [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html).
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

    // default exception handler if none of previous exceptions handled.
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

    private fun errorResponse(ex: Exception): ErrorResponse =
        ServerErrorException(ex.message ?: "Unknown error", ex)

    internal companion object {
        private val log: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }

    private fun logError(ex: Exception) {
        val errorMsg = ex.message
        val errorNativeMsg = RootCauseErrorMessageI.create(ex)
        log.error("handle exception error [$errorMsg] and native error [$errorNativeMsg]", ex)
    }
}
