package ar.edu.unq.mientradita.webservice

import ar.edu.unq.mientradita.model.exception.MiEntraditaException
import ar.edu.unq.mientradita.model.user.Spectator
import ar.edu.unq.mientradita.service.AuthUserService
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
        return try {
            val responseHeaders = HttpHeaders()
            val pairTokenUser = authUserService.login(loginRequest)
            responseHeaders.set("Authorization", pairTokenUser.first)

            ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(pairTokenUser.second)

        } catch (exception: MiEntraditaException) {
            ResponseEntity.badRequest().body(exception.toMap())
        }
    }

    @RequestMapping(value = ["/register"], method = [RequestMethod.POST])
    fun register(@RequestBody @Valid registerRequest: RegisterRequest): ResponseEntity<*> {
        return try {
            ResponseEntity(authUserService.createSpectator(registerRequest), HttpStatus.CREATED)
        } catch (exception: MiEntraditaException) {
            ResponseEntity.badRequest().body(exception.toMap())
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): Map<String, String?>? {
        val errors: MutableMap<String, String?> = HashMap()
        ex.bindingResult.fieldErrors.forEach(Consumer { error: FieldError -> errors[error.field] = error.defaultMessage })
        return errors
    }

}

data class LoginRequest(
        @field:NotBlank(message = "The username field cannot be empty")
        val username: String,
        @field:NotBlank(message = "The password field cannot be empty ")
        val password: String)

data class RegisterRequest(
        @field:NotBlank(message = "The field name cannot be empty")
        @field:Pattern(regexp = "^[a-zA-Z]*$", message = "The name can only be made up of letters")
        val name: String,
        @field:NotBlank(message = "The field surname cannot be empty")
        @field:Pattern(regexp = "^[a-zA-Z]*$", message = "The surname can only be made up of letters")
        val surname: String,
        @field:NotBlank(message = "The username field cannot be empty")
        @field:Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Username cannot contain spaces")
        val username: String,
        @field:NotBlank(message = "The field password cannot be empty")
        @field:Min(value = 6, message = "The password must be more than 6 characters")
        val password: String,
        @field:NotBlank(message = "The dni field cannot be empty")
        @field:Pattern(regexp = "^[0-9]{8}$", message = "The dni cannot contain letters")
        val dni: String,
        @field:NotBlank(message = "The email field cannot be empty")
        @field:Email
        @field:Pattern(regexp = "^[^@]+@[^@]+\\.[a-zA-Z]{2,}\$", message = "The email must be of the style 'user1234@gmail.com' ")
        val email: String
) {
    fun toModel() = Spectator(name, surname, username, email, dni, password)
}