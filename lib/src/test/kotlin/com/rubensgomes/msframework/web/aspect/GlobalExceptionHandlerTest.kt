package com.rubensgomes.msframework.web.aspect

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

// requires @SpringBootTest to avoid:
// HttpMediaTypeNotAcceptableException: No acceptable representation
@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {
    @Autowired private lateinit var mockMvc: MockMvc

    @Test
    fun `fails due to handleBadRequest`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/badrequest").accept(MediaType.APPLICATION_JSON),
            ).andDo(print())
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `fails due to handleNotFound`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/notfound").accept(MediaType.APPLICATION_JSON),
            ).andDo(print())
            .andExpect(status().isNotFound)
    }

    @Test
    fun `fails due to handleMethodNotAllowed`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.patch("/methodNotAllowed").accept(MediaType.APPLICATION_JSON),
            ).andDo(print())
            .andExpect(status().isMethodNotAllowed)
    }

    @Test
    fun `fails due to handleNotAcceptable`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.patch("/notAcceptable").accept(MediaType.APPLICATION_JSON),
            ).andDo(print())
            .andExpect(status().isNotAcceptable)
    }

    // TODO add further tests
}
