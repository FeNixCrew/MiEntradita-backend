package ar.edu.unq.mientradita.webservice.controllers

import ar.edu.unq.mientradita.model.exception.MiEntraditaException
import ar.edu.unq.mientradita.model.user.Spectator
import ar.edu.unq.mientradita.service.AuthUserService
import org.hibernate.validator.constraints.Range
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.util.function.Consumer
import javax.validation.Valid
import javax.validation.constraints.*


@RestController
@RequestMapping("/api/auth")
@EnableAutoConfiguration
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

data class LoginRequest(
        @field:NotBlank(message = "El usuario es requerido")
        val username: String,
        @field:NotBlank(message = "La contrasenia es requerida")
        val password: String)

data class RegisterRequest(
        @field:NotBlank(message = "El nombre es requerido")
        @field:Pattern(regexp = "^[a-zA-Z]*$", message = "El nombre solo puede contener letras")
        val name: String,
        @field:NotBlank(message = "El apellido es requerido")
        @field:Pattern(regexp = "^[a-zA-Z]*$", message = "El apellido solo puede contener letras")
        val surname: String,
        @field:NotBlank(message = "El nombre de usuario es requerido")
        @field:Pattern(regexp = "^[a-zA-Z0-9]*$", message = "El nombre de usuario solo puede contener caracteres alfanumericos y sin espacios")
        val username: String,
        @field:NotBlank(message = "La contrasenia es requerida")
        @field:Pattern(regexp = "^.{6,}\$", message = "La contrasenia debe poseer al menos 6 caracteres")
        val password: String,
        @field:NotNull(message = "El dni es requerido")
        @field:Range(min = 1000000, max = 99999999, message = "El dni ingresado no es valido. Debe contener entre 7 y 8 digitos")
        val dni: Int,
        @field:NotBlank(message = "El correo es requerido")
        @field:Email(message = "Correo invalido. Por favor intente nuevamente.")
        @field:Pattern(regexp = "^[^@]+@[^@]+\\.[a-zA-Z]{2,}\$", message = "El correo ingresado debe poseer el siguiente formato 'usuario@ejemplo.com'")
        val email: String
) {
    fun toModel() = Spectator(name, surname, username, email, dni, password)
}