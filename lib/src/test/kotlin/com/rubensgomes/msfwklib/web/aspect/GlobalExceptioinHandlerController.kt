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

import com.rubensgomes.msreqresplib.BaseResponse
import jakarta.validation.ValidationException
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.NoHandlerFoundException

/**
 * Class used to unit test the [GlobalExceptionHandler].
 *
 * @author Rubens Gomes
 */
@RestController
class GlobalExceptioinHandlerController {
  @RequestMapping(
      value = ["/badrequest"],
      method = [RequestMethod.GET],
      consumes = [MediaType.ALL_VALUE],
      produces = [MediaType.APPLICATION_JSON_VALUE],
  )
  fun badrequest(): ResponseEntity<BaseResponse> {
    val ex = ValidationException("badrequest")
    throw ex
  }

  @RequestMapping(
      value = ["/notfound"],
      method = [RequestMethod.GET],
      consumes = [MediaType.ALL_VALUE],
      produces = [MediaType.APPLICATION_JSON_VALUE],
  )
  fun notfound(): ResponseEntity<BaseResponse> {
    val httpMethod = "CONNECT"
    val requestURL = "http://localhost:8080/notfound"
    val httpHeaders = HttpHeaders()
    throw NoHandlerFoundException(httpMethod, requestURL, httpHeaders)
  }

  @RequestMapping(
      value = ["/methodNotAllowed"],
      method = [RequestMethod.GET],
      consumes = [MediaType.ALL_VALUE],
      produces = [MediaType.APPLICATION_JSON_VALUE],
  )
  fun methodNotAllowed(): ResponseEntity<BaseResponse> {
    val httpMethod = "CONNECT"
    throw HttpRequestMethodNotSupportedException(httpMethod)
  }

  @RequestMapping(
      value = ["/notAcceptable"],
      method = [RequestMethod.GET],
      consumes = [MediaType.ALL_VALUE],
      produces = [MediaType.APPLICATION_JSON_VALUE],
  )
  fun notAcceptable(): ResponseEntity<BaseResponse> =
      throw HttpMediaTypeNotAcceptableException("media type not acceptable")
}
