package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.exception.InvalidCredentialsException
import ar.edu.unq.mientradita.model.exception.UsernameAlreadyRegistered
import ar.edu.unq.mientradita.model.user.Spectator
import ar.edu.unq.mientradita.persistence.SpectatorRepository
import ar.edu.unq.mientradita.webservice.LoginRequest
import ar.edu.unq.mientradita.webservice.RegisterRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthUserService {

    @Autowired
    private lateinit var spectatorRepository: SpectatorRepository

    @Transactional
    fun createSpectator(registerRequest: RegisterRequest): SpectatorDTO {
        val maybeSpectator = spectatorRepository.findByUsername(registerRequest.username)
        if (maybeSpectator != null) {
            throw UsernameAlreadyRegistered()
        }
        val spectator = registerRequest.toModel()
        spectatorRepository.save(spectator)

        return SpectatorDTO.fromModel(spectator)
    }

    @Transactional
    fun login(loginRequest: LoginRequest): SpectatorDTO {
        val maybeSpectator = spectatorRepository.findByUsernameAndPassword(loginRequest.username, loginRequest.password)

        if (maybeSpectator != null) {
            return SpectatorDTO.fromModel(maybeSpectator)
        } else {
            throw InvalidCredentialsException()
        }
    }

}

data class SpectatorDTO(val id: Long, val username: String) {
    companion object {
        fun fromModel(spectator: Spectator): SpectatorDTO {
            return SpectatorDTO(spectator.id!!, spectator.username)
        }
    }
}