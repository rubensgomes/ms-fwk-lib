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
package com.rubensgomes.msfwklib.common

import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException

private val log = KotlinLogging.logger {}

/**
 * A static factory method to resolve the root cause error message from a given [Throwable]
 * exception instance.
 *
 * @author Rubens Gomes
 */
object RootErrorMessageResolver {
    /** A factory static method to resolve the root cause error message from the given exception. */
    fun resolveMessage(ex: Throwable): String {
        log.debug { "resolving root cause message from ex: $ex::class.simpleName" }
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
