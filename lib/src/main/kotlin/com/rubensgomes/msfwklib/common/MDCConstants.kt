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

    val MDC_KEYS = setOf(CLIENT_ID_KEY, TRANSACTION_ID_KEY)
}
