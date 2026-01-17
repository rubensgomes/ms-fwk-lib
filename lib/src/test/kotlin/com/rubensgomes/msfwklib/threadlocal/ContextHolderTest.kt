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
package com.rubensgomes.msfwklib.threadlocal

import org.apache.commons.lang3.StringUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ContextHolderTest {
  @BeforeEach
  fun clearContext() {
    ContextHolder.clear()
  }

  @Test
  fun getReturnsNullWhenContextIsEmpty() {
    assertNull(ContextHolder.get("nonexistent"))
  }

  @Test
  fun getReturnsNullWhenKeyDoesNotExist() {
    ContextHolder.put("foo", "bar")
    assertNull(ContextHolder.get("baz"))
  }

  @Test
  fun putStoresValueAndGetReturnsIt() {
    ContextHolder.put("key", "value")
    assertEquals("value", ContextHolder.get("key"))
  }

  @Test
  fun putOverwritesExistingValue() {
    ContextHolder.put("key", "first")
    ContextHolder.put("key", "second")
    assertEquals("second", ContextHolder.get("key"))
  }

  @Test
  fun clearRemovesAllValues() {
    ContextHolder.put("key1", "value1")
    ContextHolder.put("key2", "value2")
    ContextHolder.clear()
    assertNull(ContextHolder.get("key1"))
    assertNull(ContextHolder.get("key2"))
  }

  @Test
  fun getThrowsExceptionForBlankKey() {
    val exception = assertThrows(IllegalArgumentException::class.java) { ContextHolder.get("   ") }
    assertTrue(StringUtils.containsIgnoreCase(exception.message, "Key must not be blank"))
  }

  @Test
  fun putThrowsExceptionForBlankKey() {
    val exception =
        assertThrows(IllegalArgumentException::class.java) { ContextHolder.put("", "value") }
    assertTrue(StringUtils.containsIgnoreCase(exception.message, "Key must not be blank"))
  }
}
