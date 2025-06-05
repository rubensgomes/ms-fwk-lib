package com.rubensgomes.msframework.common

import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException

/**
 * A static factory method to resolve the root cause error message from a given [Throwable]
 * exception instance.
 *
 * @author Rubens Gomes
 */
object RootCauseMessage {
    /** A factory static method to resolve the root cause error message from the given exception. */
    fun create(ex: Throwable): String {
        log.debug(
            "resolving root cause message from ex: {}",
            ex::class.simpleName,
        )
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

    private val log: Logger = LoggerFactory.getLogger(RootCauseMessage::class.java)
}
