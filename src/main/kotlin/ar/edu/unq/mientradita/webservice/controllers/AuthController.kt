package ar.edu.unq.mientradita.webservice.controllers

import ar.edu.unq.mientradita.aspect.NoLogging
import ar.edu.unq.mientradita.service.AuthUserService
import ar.edu.unq.mientradita.service.dto.LoginRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid


@RestController
@RequestMapping("/api/auth")
@EnableAutoConfiguration
@NoLogging
class AuthController {
    @Autowired
    private lateinit var authUserService: AuthUserService

    @RequestMapping(value = ["/login"], method = [RequestMethod.POST])
    fun logIn(@RequestBody @Valid loginRequest: LoginRequest): ResponseEntity<*> {
        val responseHeaders = HttpHeaders()
        val pairTokenUser = authUserService.login(loginRequest)
        responseHeaders.set("Authorization", pairTokenUser.first)
        return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(pairTokenUser.second)
    }

    @RequestMapping(value = ["/register"], method = [RequestMethod.POST])
    fun register(@RequestBody @Valid registerRequest: RegisterRequest): ResponseEntity<*> {
        return ResponseEntity(authUserService.createSpectator(registerRequest), HttpStatus.CREATED)
    }

}
