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

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.context.runner.ApplicationContextRunner

/** A [@Configuration] class in the application must enable the loading of the [WebProperties]. */
@TestConfiguration
@EnableConfigurationProperties(*[WebProperties::class])
class WebPropertiesConfiguration {
  init {
    log.trace("init called.")
  }

  internal companion object {
    private val log = LoggerFactory.getLogger(WebPropertiesConfiguration::class.java)
  }
}

/**
 * This class makes use of the [ApplicationContextRunner] to provide a way to load and test vaiours
 * Spring configuration properties files. It is not possible to use `@SpringBootTest` to test
 * invalid configuration properties files because the class fails prior to the
 * [@Test} method. Therefore, we need to manually create and load the `ApplicationContext`from within the [@Test]
 * function using the [ApplicationContextRunner].
 *
 * @author Rubens Gomes
 */
class WebPropertiesConfigurationPropertiesTest {
  // I had issues when I tried to initialize the contextRunner in a
  // @BeforeTest method. The second test was failing with null for the
  // startUpFailure. This is an indication that the ApplicationContextRunner
  // is making use of class static data.
  // -- Rubens Gomes (08/16/2024)

  @Test
  fun `ensure success when app web property allowed-methods is valid`() {
    val contextRunner = ApplicationContextRunner()
    contextRunner
        // triggers loading spring configuration file (e.g., application.yml)
        .withInitializer(ConfigDataApplicationContextInitializer())
        .withUserConfiguration(WebPropertiesConfiguration::class.java)
        // configure location of spring configuration file. same as:
        // @TestPropertySource(properties =
        // ["spring.config.location=$EMPTY_METHODS_ALLOWED_CONFIG_FILE"])
        .withPropertyValues("spring.config.location=$VALID_METHODS_ALLOWED_CONFIG_FILE")
        .run { consumer -> assertNull(consumer.startupFailure) }
  }

  @Test
  fun `fail due to missing app web property allowed-methods`() {
    val contextRunner = ApplicationContextRunner()
    contextRunner
        // triggers loading spring configuration file (e.g., application.yml)
        .withInitializer(ConfigDataApplicationContextInitializer())
        .withUserConfiguration(WebPropertiesConfiguration::class.java)
        // configure location of spring configuration file. same as:
        // @TestPropertySource(properties =
        // ["spring.config.location=$EMPTY_METHODS_ALLOWED_CONFIG_FILE"])
        .withPropertyValues("spring.config.location=$EMPTY_METHODS_ALLOWED_CONFIG_FILE")
        .run { consumer -> assertNotNull(consumer.startupFailure) }
  }

  @Test
  fun `fail due to invalid app web property allowed-methods`() {
    val contextRunner = ApplicationContextRunner()
    contextRunner
        // triggers loading spring configuration file (e.g., application.yml)
        .withInitializer(ConfigDataApplicationContextInitializer())
        .withUserConfiguration(WebPropertiesConfiguration::class.java)
        // configure location of spring configuration file. same as:
        // @TestPropertySource(properties =
        // ["spring.config.location=$EMPTY_METHODS_ALLOWED_CONFIG_FILE"])
        .withPropertyValues("spring.config.location=$INVALID_METHODS_ALLOWED_CONFIG_FILE")
        .run { consumer -> assertNotNull(consumer.startupFailure) }
  }

  internal companion object {
    private const val EMPTY_METHODS_ALLOWED_CONFIG_FILE = "classpath:methods-allowed/empty.yml"
    private const val INVALID_METHODS_ALLOWED_CONFIG_FILE = "classpath:methods-allowed/invalid.yml"
    private const val VALID_METHODS_ALLOWED_CONFIG_FILE = "classpath:methods-allowed/valid.yml"
  }
}
