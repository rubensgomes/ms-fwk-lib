package com.rubensgomes.msframework.web.aspect

import com.rubensgomes.msframework.common.MDCConstants
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.MethodParameter
import org.springframework.http.HttpInputMessage
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter
import java.lang.reflect.Type
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * AOP (Aspect Oriented Programming) advice code that is applied after a web request is read from
 * the input stream. It uses Kotlin reflection to parse the request body object looking for any
 * information to add to the underlying logging logback [MDC] context.
 *
 * @author Rubens Gomes
 */
@ControllerAdvice
class RequestBodyMDCUpdateAdvice : RequestBodyAdviceAdapter() {
    override fun supports(
        methodParameter: MethodParameter,
        targetType: Type,
        converterType: Class<out HttpMessageConverter<*>>,
    ): Boolean {
        log.trace(
            "Incoming request [{}] type [{}] being processed",
            methodParameter.method,
            targetType.typeName,
        )
        return true
    }

    override fun afterBodyRead(
        body: Any,
        inputMessage: HttpInputMessage,
        parameter: MethodParameter,
        targetType: Type,
        converterType: Class<out HttpMessageConverter<*>>,
    ): Any {
        log.trace("updating MDC context.")

        // ---------- >>> Kotlin Reflection <<< --------------------------------

        val clazz = body::class
        val properties = clazz.memberProperties

        for (key in MDCConstants.MDC_KEYS) {
            log.debug(
                "searching for {} in request body class {}",
                key,
                clazz.simpleName,
            )

            try {
                // find first entry value corresponding to key in body object.
                val value =
                    properties
                        .first { it.name == key }
                        .also { it.isAccessible = true }
                        .let { it as KProperty1<in Any, *> }
                        .getter(body)

                if (value.toString().isNotBlank()) {
                    log.debug(
                        "updating MDC with non-blank value for MDC key: {} = {}",
                        key,
                        value.toString(),
                    )
                    MDC.put(key, value.toString())
                } else {
                    log.warn("value for MDC key: {} = {} is blank", key, value)
                }
            } catch (e: NoSuchElementException) {
                log.debug("no matching key {} : {}", key, e.message)
            }
        }

        return body
    }

    internal companion object {
        private val log: Logger = LoggerFactory.getLogger(RequestBodyMDCUpdateAdvice::class.java)
    }
}
