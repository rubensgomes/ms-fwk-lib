package com.rubensgomes.msframework.common

/**
 * Constant MDC key values and a list of all supported MDC keys. The list of MDC keys may be used
 * when parsing requests to look for data to populate the [org.slf4j.MDC], or when retrieving values
 * from the [org.slf4j.MDC].
 *
 * @author Rubens Gomes
 */
object MDCConstants {
    const val CLIENT_ID_KEY = "clientId"
    const val TRANSACTION_ID_KEY = "transactionId"

    val MDC_KEYS =
        listOf(
            CLIENT_ID_KEY,
            TRANSACTION_ID_KEY,
        )
}
