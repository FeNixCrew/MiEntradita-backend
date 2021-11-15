package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.exception.DniAlreadyRegistered
import ar.edu.unq.mientradita.model.exception.EmailAlreadyRegistered
import ar.edu.unq.mientradita.model.exception.InvalidCredentialsException
import ar.edu.unq.mientradita.model.exception.UsernameAlreadyRegistered
import ar.edu.unq.mientradita.persistence.UserRepository
import ar.edu.unq.mientradita.persistence.spectator.SpectatorRepository
import ar.edu.unq.mientradita.service.dto.UserDTO
import ar.edu.unq.mientradita.webservice.config.security.JWTTokenUtil
import ar.edu.unq.mientradita.service.dto.LoginRequest
import ar.edu.unq.mientradita.webservice.controllers.RegisterRequest
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
    private lateinit var jwtUtil: JWTTokenUtil

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

        return Pair(jwtUtil.generateToken(maybeSpectator.get()), UserDTO.fromModel(maybeSpectator.get()))
    }
}