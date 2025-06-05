package com.rubensgomes.msframework.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RootCauseMessageTest {
    private val obj = RootCauseMessage

    @Test
    fun `ensure root causes message matches expected message`() {
        val expected = "hello world"
        val ex = RuntimeException(expected)
        val actual = obj.create(ex)
        assertEquals(expected, actual)
    }
}
