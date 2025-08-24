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
package com.rubensgomes.msfwklib.config

import com.rubensgomes.msfwklib.web.filter.MDCClearFilter
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered

private val log = KotlinLogging.logger {}

/**
 * Spring Boot configuration class for registering and configuring servlet filters in the web server
 * filter pipeline.
 *
 * This configuration class manages the registration of various servlet filters that provide
 * cross-cutting functionality for web requests. Filters are registered as Spring beans and
 * configured with specific order precedence, URL patterns, and enabling/disabling options to ensure
 * proper request processing flow.
 *
 * ## Filter Pipeline Architecture:
 * The servlet filter chain follows a specific order of execution:
 * 1. **Security Filters** (if present)
 * 2. **MDC Clear Filter** (HIGHEST_PRECEDENCE) - Context cleanup
 * 3. **Request Processing Filters** (if present)
 * 4. **Response Processing Filters** (if present)
 *
 * ## Key Features:
 * - **Ordered Execution**: Filters are configured with specific precedence orders
 * - **URL Pattern Matching**: Filters can be applied to specific URL patterns
 * - **Conditional Registration**: Filters can be enabled/disabled based on configuration
 * - **Spring Integration**: Full integration with Spring Boot's filter registration mechanism
 * - **Memory Management**: Ensures proper cleanup of thread-local resources
 *
 * ## Registered Filters:
 * Currently, this configuration registers the following filters:
 * - [MDCClearFilter]: Cleans up MDC (Mapped Diagnostic Context) at the end of each request
 *
 * ## Usage in Applications:
 *
 * ### Automatic Registration:
 * When the framework library is included, filters are automatically registered:
 * ```kotlin
 * @SpringBootApplication
 * class MyMicroserviceApplication
 *
 * fun main(args: Array<String>) {
 *     runApplication<MyMicroserviceApplication>(*args)
 * }
 * ```
 *
 * @see org.springframework.boot.web.servlet.FilterRegistrationBean
 * @see org.springframework.core.Ordered
 * @see com.rubensgomes.msfwklib.web.filter.MDCClearFilter
 * @author Rubens Gomes
 * @since 0.0.1
 */
@Configuration
open class FilterConfiguration {
    @Bean
    open fun mdcClearFilter(): FilterRegistrationBean<MDCClearFilter> {
        log.debug { "registering MDCClearFilter" }
        val bean = FilterRegistrationBean<MDCClearFilter>()
        bean.filter = MDCClearFilter()
        bean.order = Ordered.HIGHEST_PRECEDENCE
        bean.isEnabled = true
        bean.addUrlPatterns("/*")
        bean.setName(MDCClearFilter::class.java.name)
        return bean
    }
}
