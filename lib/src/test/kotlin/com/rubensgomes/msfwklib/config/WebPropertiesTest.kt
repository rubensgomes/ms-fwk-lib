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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod

class WebPropertiesTest {
  @Test
  fun `ensure default constructor works`() {
    val data = WebProperties()
    assertNotNull(data.allowedMethods)
  }

  @Test
  fun `ensure methods allowed are configured`() {
    val expected = listOf(HttpMethod.GET.name(), HttpMethod.POST.name())
    val data = WebProperties(expected)
    assertEquals(expected, data.allowedMethods)
  }

  @Test
  fun `print the instance`() {
    val data = WebProperties()
    val text = data.toString()
    log.debug(text)
    assertNotNull(text)
  }

  @Test
  fun `print the instance properties `() {
    val data = WebProperties()
    val text = data.allowedMethods.toString()
    log.debug(text)
    assertNotNull(text)
  }

  /*
  TODO: fix code
  @Test
  fun `fail due to missing methods allowed`() {
      assertFailsWith<IllegalArgumentException> { WebProperties(emptyList()) }
  }

  @Test
  fun `fail due to invalid methods allowed`() {
      assertFailsWith<IllegalArgumentException> { WebProperties(listOf("HELLO")) }
  }
   */

  internal companion object {
    private val log = LoggerFactory.getLogger(WebPropertiesTest::class.java)
  }
}
