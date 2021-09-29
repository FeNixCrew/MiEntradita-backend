package ar.edu.unq.mientradita.webservice

import ar.edu.unq.mientradita.model.exception.MiEntraditaException
import ar.edu.unq.mientradita.model.user.Spectator
import ar.edu.unq.mientradita.service.AuthUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController {
    @Autowired
    private lateinit var authUserService: AuthUserService

    @RequestMapping(value = ["/login"], method = [RequestMethod.POST])
    fun logIn(@RequestBody loginRequest: LoginRequest): ResponseEntity<*> {
        return try {
            ResponseEntity.ok(authUserService.login(loginRequest))
        } catch (exception: MiEntraditaException) {
            ResponseEntity.badRequest().body(exception.toMap())
        }
    }

    @RequestMapping(value = ["/register"], method = [RequestMethod.POST])
    fun register(@RequestBody registerRequest: RegisterRequest): ResponseEntity<*> {
        return try {
            ResponseEntity(authUserService.createSpectator(registerRequest), HttpStatus.CREATED)
        } catch (exception: MiEntraditaException) {
            ResponseEntity.badRequest().body(exception.toMap())
        }
    }

}

data class LoginRequest(val username: String, val password: String)

data class RegisterRequest(
    val name: String,
    val surname: String,
    val username: String,
    val password: String,
    val dni: Int,
    val email: String
) {
    fun toModel() = Spectator(name, surname, username, email, dni, password)
}