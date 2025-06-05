package com.rubensgomes.msframework.config

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
        val expected =
            listOf(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
            )
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
