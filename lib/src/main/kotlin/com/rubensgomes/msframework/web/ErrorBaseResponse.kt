package com.rubensgomes.msframework.web

import com.rubensgomes.msframework.common.MDCConstants
import com.rubensgomes.msframework.common.RootCauseMessage
import com.rubensgomes.reqresp.BaseResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC

/**
 * An [BaseResponse] error object that is created as part of handling exceptions in web
 * applications. This class is expected to be used as the body type of a
 * [org.springframework.http.ResponseEntity].
 *
 * Notice that the only way to create an instance of this class is by using is factory static
 * method. That's because some of its properties (e.g., clientId, and transactionId) are being
 * enforced to be derived from the [MDC] context.
 *
 * @author Rubens Gomes
 */
class ErrorBaseResponse
    private constructor(
        clientId: String,
        transactionId: String,
        status: Status,
        message: String,
        rootErrorCause: String? = null,
    ) : BaseResponse(clientId, transactionId, status, message, rootErrorCause) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false
            return true
        }

        override fun toString(): String = "$${this.javaClass.name}: " + " ${super.toString()}"

        internal companion object {
            private val log: Logger = LoggerFactory.getLogger(ErrorBaseResponse::class.java)

            /** static factory method to create an instance of this class. */
            fun create(
                message: String,
                ex: Throwable,
                status: BaseResponse.Status,
            ): ErrorBaseResponse {
                val clientId =
                    if (MDC.get(MDCConstants.CLIENT_ID_KEY).isNullOrBlank()) {
                        "clientId not found in MDC context"
                    } else {
                        MDC.get(MDCConstants.CLIENT_ID_KEY)
                    }

                val transactionId =
                    if (MDC.get(MDCConstants.TRANSACTION_ID_KEY).isNullOrBlank()) {
                        "transactionId not found in MDC context"
                    } else {
                        MDC.get(MDCConstants.TRANSACTION_ID_KEY)
                    }

                val rootErrorCause = RootCauseMessage.create(ex)

                return ErrorBaseResponse(
                    clientId,
                    transactionId,
                    status,
                    message,
                    rootErrorCause,
                )
            }

            fun create(
                ex: Throwable,
                status: BaseResponse.Status,
            ): ErrorBaseResponse {
                val message =
                    if (ex.message.isNullOrBlank()) {
                        log.warn("no message found in ex {}", ex::class.simpleName)
                        "unknown error"
                    } else {
                        ex.message.toString()
                    }

                return create(message, ex, status)
            }
        }
    }
