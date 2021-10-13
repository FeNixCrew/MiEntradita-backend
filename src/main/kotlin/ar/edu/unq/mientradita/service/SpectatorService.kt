package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.Ticket
import ar.edu.unq.mientradita.model.exception.MatchDoNotExistsException
import ar.edu.unq.mientradita.model.exception.SpectatorNotRegistered
import ar.edu.unq.mientradita.persistence.MatchRepository
import ar.edu.unq.mientradita.persistence.SpectatorRepository
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
    fun reserveTicket(spectatorId: Long, matchId: Long, reserveTicketTime: LocalDateTime = LocalDateTime.now()): TicketDTO {
        val match = matchRepository.findById(matchId).orElseThrow { MatchDoNotExistsException() }

        val spectator = spectatorRepository.findById(spectatorId).orElseThrow { SpectatorNotRegistered() }

        spectator.reserveATicketFor(match, reserveTicketTime)

        matchRepository.save(match)
        val updatedSpectator = spectatorRepository.save(spectator)

        return TicketDTO.fromModel(spectatorId, updatedSpectator.findTicketFrom(match))
    }

    @Transactional
    fun pendingTickets(spectatorId: Long, aTime: LocalDateTime = LocalDateTime.now()): List<TicketDTO> {
        val spectator = spectatorRepository.findById(spectatorId).orElseThrow { SpectatorNotRegistered() }

        val pendingTickets = spectator.tickets.filter { it.isPendingAt(aTime)}

        return pendingTickets.map { ticket -> TicketDTO.fromModel(spectatorId, ticket) }
    }
}

data class TicketDTO(val id:Long, val userId: Long, val matchId: Long, val home: String, val away: String, val matchStartTime: LocalDateTime) {
    companion object {
        fun fromModel(spectatorId: Long, ticket: Ticket): TicketDTO {
            return TicketDTO(ticket.id!!, spectatorId, ticket.match.id!!, ticket.match.home.name, ticket.match.away.name, ticket.match.matchStartTime)
        }
    }
}