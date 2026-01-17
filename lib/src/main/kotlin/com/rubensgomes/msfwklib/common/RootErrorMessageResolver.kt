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

import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException

private val log = KotlinLogging.logger {}

/**
 * Utility object for resolving and extracting meaningful error messages from various types of
 * exceptions.
 *
 * This resolver provides a centralized way to extract root cause error messages from different
 * exception types, with special handling for Spring validation exceptions and generic throwables.
 * It's particularly useful in global exception handlers where consistent error message formatting
 * is required.
 *
 * The resolver handles the following exception types specifically:
 * - [BindException]: Extracts field validation error messages
 * - [MethodArgumentNotValidException]: Extracts detailed validation errors including field names
 *   and rejected values
 * - Generic [Throwable]: Extracts the root cause message using Apache Commons Lang
 *
 * Usage examples:
 * ```kotlin
 * // For validation exceptions
 * val validationEx = MethodArgumentNotValidException(...)
 * val message = RootErrorMessageResolver.resolveMessage(validationEx)
 * // Returns: "Field [email]\nValue [invalid-email]\nEmail must be valid\n"
 *
 * // For generic exceptions
 * val runtimeEx = RuntimeException("Something went wrong", IOException("File not found"))
 * val message = RootErrorMessageResolver.resolveMessage(runtimeEx)
 * // Returns: "File not found" (the root cause message)
 *
 * // For bind exceptions
 * val bindEx = BindException(...)
 * val message = RootErrorMessageResolver.resolveMessage(bindEx)
 * // Returns: "Field is required\nValue must be positive\n"
 * ```
 *
 * This utility is commonly used in:
 * - Global exception handlers ([org.springframework.web.bind.annotation.ControllerAdvice])
 * - Error logging mechanisms
 * - API error response generation
 * - Debugging and troubleshooting scenarios
 *
 * @see org.apache.commons.lang3.exception.ExceptionUtils
 * @see org.springframework.validation.BindException
 * @see org.springframework.web.bind.MethodArgumentNotValidException
 * @author Rubens Gomes
 * @since 0.0.1
 */
object RootErrorMessageResolver {

  /**
   * Resolves and extracts a meaningful error message from the given exception.
   *
   * This method analyzes the provided exception and extracts error messages based on the exception
   * type:
   *
   * **For [BindException]:**
   * - Iterates through all field errors
   * - Concatenates default error messages with newline separators
   * - Returns formatted validation error messages
   *
   * **For [MethodArgumentNotValidException]:**
   * - Extracts detailed validation information including field names and rejected values
   * - Formats each error with field name, rejected value, and error message
   * - Provides comprehensive validation failure details
   *
   * **For other [Throwable] types:**
   * - Uses Apache Commons Lang [ExceptionUtils.getRootCause] to find the root cause
   * - Returns the message from the deepest cause in the exception chain
   * - Handles nested exceptions by unwrapping to the original cause
   *
   * @param ex The exception from which to extract the error message. Must not be null.
   * @return A formatted error message string. For validation exceptions, may contain multiple lines
   *   with detailed field information. For generic exceptions, returns the root cause message.
   *   Never returns null, but may return an empty string if no message is available.
   * @throws IllegalArgumentException if the provided exception is null (implicitly handled by
   *   Kotlin)
   * @see org.apache.commons.lang3.exception.ExceptionUtils.getRootCause
   * @see org.springframework.validation.FieldError.getDefaultMessage
   */
  fun resolveMessage(ex: Throwable): String {
    log.debug { "resolving root cause message from ex: ${ex::class.simpleName}" }
    val buff = StringBuffer()

    when (ex) {
      is BindException -> {
        for (field in ex.fieldErrors) {
          buff.append(field.defaultMessage).append("\n")
        }
      }

      is MethodArgumentNotValidException -> {
        for (error in ex.bindingResult.allErrors) {
          if (error is FieldError) {
            buff.append("Field [").append(error.field).append("]\n")
            buff.append("Value [").append(error.rejectedValue).append("]\n")
          }

          buff.append(error.defaultMessage).append("\n")
        }
      }

      else -> {
        val rootCause = ExceptionUtils.getRootCause(ex)
        buff.append(rootCause.message)
      }
    }

    return buff.toString()
  }
}
