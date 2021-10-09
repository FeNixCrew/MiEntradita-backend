package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.exception.DniAlreadyRegistered
import ar.edu.unq.mientradita.model.exception.EmailAlreadyRegistered
import ar.edu.unq.mientradita.model.exception.InvalidCredentialsException
import ar.edu.unq.mientradita.model.exception.UsernameAlreadyRegistered
import ar.edu.unq.mientradita.model.user.User
import ar.edu.unq.mientradita.persistence.SpectatorRepository
import ar.edu.unq.mientradita.persistence.UserRepository
import ar.edu.unq.mientradita.webservice.LoginRequest
import ar.edu.unq.mientradita.webservice.RegisterRequest
import ar.edu.unq.mientradita.webservice.config.security.JWTUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthUserService {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var spectatorRepository: SpectatorRepository

    @Autowired
    private lateinit var jwtUtil: JWTUtil

    @Transactional
    fun createSpectator(registerRequest: RegisterRequest): UserDTO {
        if (userRepository.findByUsernameIgnoreCase(registerRequest.username).isPresent) {
            throw UsernameAlreadyRegistered()
        }

        if(userRepository.findByEmailIgnoreCase(registerRequest.email).isPresent) {
            throw EmailAlreadyRegistered()
        }

        if(spectatorRepository.findByDni(registerRequest.dni).isPresent) {
            throw DniAlreadyRegistered()
        }

        val spectator = registerRequest.toModel()
        userRepository.save(spectator)

        return UserDTO.fromModel(spectator)
    }

    @Transactional
    fun login(loginRequest: LoginRequest): Pair<String, UserDTO> {
        val maybeSpectator = userRepository.findByUsernameAndPassword(loginRequest.username, loginRequest.password)
        if (!maybeSpectator.isPresent) {
            throw InvalidCredentialsException()
        }

        return Pair(jwtUtil.generateToken(maybeSpectator.get()),UserDTO.fromModel(maybeSpectator.get()))
    }
}

data class UserDTO(val id: Long, val username: String, val role: String) {
    companion object {
        fun fromModel(user: User): UserDTO {
            return UserDTO(user.id!!, user.username, user.role.toString())
        }
    }
}