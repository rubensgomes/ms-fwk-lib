package com.rubensgomes.msframework

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.boot.SpringApplication
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MsFrameworkAppTest {
    @Test
    fun `ensure spring context loads`() {
        val context = SpringApplication.run(MsFrameworkApp::class.java)
        assertNotNull(context)
    }
}
