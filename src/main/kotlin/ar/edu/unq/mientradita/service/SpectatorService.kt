package ar.edu.unq.mientradita.service

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

@Service
class SpectatorService {

    @Autowired
    private lateinit var spectatorRepository: SpectatorRepository

    @Autowired
    private lateinit var matchRepository: MatchRepository

    @Transactional
    fun createSpectator(name: String, surname: String, username: String, password: String, email: String, dni: Int): Spectator {
        val spectator = Spectator(name, surname, username, email, dni, password)
        return spectatorRepository.save(spectator)
    }

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
    fun reserveTicket(spectatorId: Long, matchId: Long, reserveTicketTime: LocalDateTime = LocalDateTime.now()): TicketDTO {
        val match = matchRepository.findById(matchId).orElseThrow { MatchDoNotExistsException() }

        val spectator = spectatorRepository.findById(spectatorId).orElseThrow { SpectatorNotRegistered() }

        spectator.reserveATicketFor(match, reserveTicketTime)

        matchRepository.save(match)
        val ticket = spectatorRepository.save(spectator).findTicketFrom(match)
        return TicketDTO.fromModel(ticket)
    }

    @Transactional
    fun pendingTickets(spectatorId: Long, aTime: LocalDateTime = LocalDateTime.now()): List<TicketDTO> {
        val spectator = spectatorRepository.findById(spectatorId).orElseThrow { SpectatorNotRegistered() }

        val pendingTickets = spectator.tickets.filter { it.isPendingAt(aTime)}

        return pendingTickets.map { ticket -> TicketDTO.fromModel(ticket) }
    }
}

data class SpectatorDTO(val id: Long, val username: String) {
    companion object {
        fun fromModel(spectator: Spectator): SpectatorDTO {
            return SpectatorDTO(spectator.id!!, spectator.username)
        }
    }
}

data class TicketDTO(val id:Long, val matchId: Long, val home: String, val away: String, val matchStartTime: LocalDateTime) {
    companion object {
        fun fromModel(ticket: Ticket): TicketDTO {
            return TicketDTO(ticket.id!!, ticket.match.id!!, ticket.match.home, ticket.match.away, ticket.match.matchStartTime)
        }
    }
}