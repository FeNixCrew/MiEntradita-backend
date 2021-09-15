package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.Spectator
import ar.edu.unq.mientradita.model.Ticket
import ar.edu.unq.mientradita.model.exception.MatchDoNotExistsException
import ar.edu.unq.mientradita.model.exception.SpectatorNotRegistered
import ar.edu.unq.mientradita.persistence.MatchRepository
import ar.edu.unq.mientradita.persistence.SpectatorRepository
import ar.edu.unq.mientradita.webservice.LoginRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.NoSuchElementException

@Service
class SpectatorService {

    @Autowired
    private lateinit var spectatorRepository: SpectatorRepository

    @Autowired
    private lateinit var matchRepository: MatchRepository

    @Transactional
    fun login(loginRequest: LoginRequest): SpectatorDTO {
        val maybeSpectator = spectatorRepository.findByUsernameAndPassword(loginRequest.username, loginRequest.password)

        if (maybeSpectator!=null) {
            return SpectatorDTO.fromModel(maybeSpectator)
        } else {
            throw RuntimeException("Las credenciales introducidas son incorrectas, intente de nuevo.")
        }
    }

    @Transactional
    fun createSpectator(name: String, surname: String, username: String, password: String, email: String, dni: Int): Spectator {
        val spectator = Spectator(name, surname, username, email, dni, password)
        return spectatorRepository.save(spectator)
    }

    @Transactional
    fun findSpectatorById(id: Long): Spectator {
        return spectatorRepository.findById(id).get()
    }

    @Transactional
    fun reserveTicket(spectatorId: Long, matchId: Long, reserveTicketTime: LocalDateTime): Spectator {
        val match = matchRepository.findById(matchId).orElseThrow { MatchDoNotExistsException() }

        val spectator = spectatorRepository.findById(spectatorId).orElseThrow { SpectatorNotRegistered() }

        spectator.reserveATicketFor(match, reserveTicketTime)

        matchRepository.save(match)
        return spectatorRepository.save(spectator)
    }

    @Transactional
    fun findTicketFrom(spectatorId: Long, matchId: Long): Ticket {
        val spectator = spectatorRepository.findById(spectatorId).get()
        val match = matchRepository.findById(matchId).get()

        return spectator.findTicketFrom(match)
    }
}

data class SpectatorDTO(val id: Long, val username: String, val role: String) {
    companion object {
        fun fromModel(spectator: Spectator): SpectatorDTO {
            return SpectatorDTO(spectator.id!!, spectator.username, spectator.role)
        }
    }
}