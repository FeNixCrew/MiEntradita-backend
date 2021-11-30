package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.exception.*
import ar.edu.unq.mientradita.model.user.Spectator
import ar.edu.unq.mientradita.persistence.UserRepository
import ar.edu.unq.mientradita.persistence.spectator.SpectatorRepository
import ar.edu.unq.mientradita.service.dto.UserDTO
import ar.edu.unq.mientradita.webservice.config.security.JWTTokenUtil
import ar.edu.unq.mientradita.service.dto.LoginRequest
import ar.edu.unq.mientradita.webservice.controllers.RegisterRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthUserService {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var spectatorRepository: SpectatorRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var jwtUtil: JWTTokenUtil

    @Transactional
    fun createSpectator(registerRequest: RegisterRequest): UserDTO {
        if (userRepository.findByUsernameIgnoreCase(registerRequest.username).isPresent) {
            throw AlreadyExistsException("Nombre de usuario ya registrado")
        }

        if (userRepository.findByEmailIgnoreCase(registerRequest.email).isPresent) {
            throw AlreadyExistsException("El correo ya esta registrado")
        }

        if (spectatorRepository.findByDni(registerRequest.dni).isPresent) {
            throw AlreadyExistsException("Ya hay una persona registrada con el dni brindado")
        }

        val spectator = userRepository.save(
            Spectator(
                registerRequest.name,
                registerRequest.surname,
                registerRequest.username,
                registerRequest.email,
                registerRequest.dni,
                passwordEncoder.encode(registerRequest.password)
            )
        )

        return UserDTO.fromModel(spectator)
    }

    @Transactional
    fun login(loginRequest: LoginRequest): Pair<String, UserDTO> {
        val spectator = userRepository.findByUsername(loginRequest.username)

        if (!spectator.isPresent || !passwordEncoder.matches(loginRequest.password, spectator.get().password)) {
            throw BusinessException("Las credenciales introducidas son incorrectas, intente de nuevo")
        }

        return Pair(jwtUtil.generateToken(spectator.get()), UserDTO.fromModel(spectator.get()))
    }
}
