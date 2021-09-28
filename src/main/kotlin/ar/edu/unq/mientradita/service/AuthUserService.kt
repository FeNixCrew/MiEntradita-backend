package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.exception.InvalidCredentialsException
import ar.edu.unq.mientradita.model.exception.UsernameAlreadyRegistered
import ar.edu.unq.mientradita.model.user.User
import ar.edu.unq.mientradita.persistence.UserRepository
import ar.edu.unq.mientradita.webservice.LoginRequest
import ar.edu.unq.mientradita.webservice.RegisterRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthUserService {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Transactional
    fun createSpectator(registerRequest: RegisterRequest): UserDTO {
        val maybeSpectator = userRepository.findByUsername(registerRequest.username)
        if (maybeSpectator.isPresent) {
            throw UsernameAlreadyRegistered()
        }

        val spectator = registerRequest.toModel()
        userRepository.save(spectator)

        return UserDTO.fromModel(spectator)
    }

    @Transactional
    fun login(loginRequest: LoginRequest): UserDTO {
        val maybeSpectator = userRepository.findByUsernameAndPassword(loginRequest.username, loginRequest.password)
        if (!maybeSpectator.isPresent) {
            throw InvalidCredentialsException()
        }

        return UserDTO.fromModel(maybeSpectator.get())
    }
}

data class UserDTO(val id: Long, val username: String, val role: String) {
    companion object {
        fun fromModel(user: User): UserDTO {
            return UserDTO(user.id!!, user.username, user.role.toString())
        }
    }
}