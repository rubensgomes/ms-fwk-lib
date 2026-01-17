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
package com.rubensgomes.msfwklib.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * Central Spring Boot configuration class that provides common framework-wide settings and
 * component registration.
 *
 * This configuration class serves as the primary entry point for framework-level configurations
 * that should be applied to all applications using the microservice framework library. It
 * centralizes the setup of configuration properties, bean definitions, and other cross-cutting
 * concerns that are commonly needed across microservices.
 *
 * ## Key Responsibilities:
 * - **Configuration Properties Enablement**: Activates Spring Boot configuration properties binding
 * - **Common Bean Registration**: Provides framework-wide bean definitions and component scanning
 * - **Cross-Cutting Concerns**: Manages aspects like security, validation, and web configurations
 * - **Framework Integration**: Establishes the foundation for other framework components
 *
 * ## Enabled Configuration Properties:
 * This class currently enables the following configuration property classes:
 * - [WebProperties]: Web-related settings including HTTP method restrictions and CORS configuration
 *
 * ## Usage in Applications:
 *
 * ### Automatic Import (Recommended):
 * When using the framework library as a dependency, this configuration is typically auto-configured
 * through Spring Boot's auto-configuration mechanism:
 * ```kotlin
 * @SpringBootApplication
 * class MyMicroserviceApplication
 *
 * fun main(args: Array<String>) {
 *     runApplication<MyMicroserviceApplication>(*args)
 * }
 * ```
 *
 * ### Manual Import:
 * If auto-configuration is disabled or you need explicit control, you can manually import this
 * configuration:
 * ```kotlin
 * @SpringBootApplication
 * @Import(CommonConfiguration::class)
 * class MyMicroserviceApplication
 *
 * fun main(args: Array<String>) {
 *     runApplication<MyMicroserviceApplication>(*args)
 * }
 * ```
 *
 * ### Custom Configuration Extension:
 * Applications can extend this configuration for application-specific needs:
 * ```kotlin
 * @Configuration
 * class ApplicationConfiguration : CommonConfiguration() {
 *
 *     @Bean
 *     fun customBean(): CustomService {
 *         return CustomService()
 *     }
 * }
 * ```
 *
 * ## Configuration Properties Integration:
 * The enabled configuration properties can be customized through standard Spring Boot configuration
 * files:
 * ```yaml
 * # application.yml
 * app:
 *   web:
 *     allowed-methods:
 *       - GET
 *       - POST
 *       - PUT
 *       - DELETE
 * ```
 *
 * ## Framework Components Integration:
 * This configuration works in conjunction with other framework components:
 * - **Web Filters**: HTTP method validation and request processing
 * - **Exception Handlers**: Global error handling and response formatting
 * - **Security Components**: Authentication and authorization configurations
 * - **Validation**: Request and response validation frameworks
 * - **Logging**: Structured logging and MDC (Mapped Diagnostic Context) setup
 *
 * ## Development and Testing:
 *
 * ### Test Configuration:
 * For testing scenarios, you can override or supplement this configuration:
 * ```kotlin
 * @TestConfiguration
 * class TestCommonConfiguration {
 *
 *     @Bean
 *     @Primary
 *     fun testWebProperties(): WebProperties {
 *         return WebProperties(allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH"))
 *     }
 * }
 * ```
 *
 * ### Configuration Profiles:
 * Different environments can have different configurations through Spring profiles:
 * ```yaml
 * # application-dev.yml (Development)
 * app:
 *   web:
 *     allowed-methods: [GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD]
 *
 * # application-prod.yml (Production)
 * app:
 *   web:
 *     allowed-methods: [GET, POST]
 * ```
 *
 * ## Extensibility:
 * As the framework evolves, additional configuration properties and beans will be added to this
 * class:
 * ```kotlin
 * // Future expansion example:
 * @Configuration
 * @EnableConfigurationProperties(
 *     WebProperties::class,
 *     SecurityProperties::class,
 *     MetricsProperties::class
 * )
 * class CommonConfiguration {
 *     // Additional bean definitions will be added here
 * }
 * ```
 *
 * ## Performance Considerations:
 * - Configuration classes are instantiated once during Spring context initialization
 * - Property binding occurs at startup time, ensuring fail-fast behavior for invalid configurations
 * - Bean creation follows Spring's lazy initialization patterns where applicable
 *
 * ## Troubleshooting:
 * Common issues and solutions:
 *
 * ### Configuration Not Loaded:
 * Ensure the framework library is on the classpath and auto-configuration is enabled
 *
 * ### Property Binding Errors:
 * Check that property names in configuration files match the expected format (e.g.,
 * `app.web.allowed-methods`)
 *
 * ### Bean Creation Failures:
 * Review application logs for validation errors in configuration properties during startup
 *
 * @see WebProperties
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.boot.context.properties.EnableConfigurationProperties
 * @author Rubens Gomes
 * @since 0.0.1
 */
@Configuration @EnableConfigurationProperties(WebProperties::class) class CommonConfiguration
