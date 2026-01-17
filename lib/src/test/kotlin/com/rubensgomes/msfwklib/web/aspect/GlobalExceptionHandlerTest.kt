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
package com.rubensgomes.msfwklib.web.aspect

import com.rubensgomes.msfwklib.TestApplication
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

// requires @SpringBootTest to avoid:
// HttpMediaTypeNotAcceptableException: No acceptable representation
@SpringBootTest(classes = [TestApplication::class])
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {
  @Autowired private lateinit var mockMvc: MockMvc

  @Test
  fun `fails due to handleBadRequest`() {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/badrequest").accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isBadRequest)
  }

  @Test
  fun `fails due to handleNotFound`() {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/notfound").accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNotFound)
  }

  @Test
  fun `fails due to handleMethodNotAllowed`() {
    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/methodNotAllowed").accept(MediaType.APPLICATION_JSON)
        )
        .andDo(print())
        .andExpect(status().isMethodNotAllowed)
  }

  @Test
  fun `fails due to handleNotAcceptable`() {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/notAcceptable").accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNotAcceptable)
  }

  // TODO add further tests
}
