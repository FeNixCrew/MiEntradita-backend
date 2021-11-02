package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.Ticket
import ar.edu.unq.mientradita.model.exception.MatchDoNotExistsException
import ar.edu.unq.mientradita.model.exception.SpectatorNotRegistered
import ar.edu.unq.mientradita.model.exception.TeamNotRegisteredException
import ar.edu.unq.mientradita.model.user.Spectator
import ar.edu.unq.mientradita.persistence.match.MatchRepository
import ar.edu.unq.mientradita.persistence.spectator.SpectatorRepository
import ar.edu.unq.mientradita.persistence.TeamRepository
import ar.edu.unq.mientradita.persistence.match.MailAndMatch
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

    @Autowired
    private lateinit var teamRepository: TeamRepository

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

        val pendingTickets = spectator.tickets.filter { it.isPendingAt(aTime) }

        return pendingTickets.map { ticket -> TicketDTO.fromModel(spectatorId, ticket) }
    }

    @Transactional
    fun favouriteTeamFor(spectatorId: Long): TeamDTO? {
        val spectator = spectatorRepository.findById(spectatorId).orElseThrow { SpectatorNotRegistered() }

        return if (spectator.hasFavouriteTeam()) {
            TeamDTO.fromModel(spectator.favouriteTeam!!)
        } else {
            null
        }
    }

    @Transactional
    fun markAsFavourite(spectatorId: Long, teamId: Long): TeamDTO? {
        val spectator = spectatorRepository.findById(spectatorId).orElseThrow { SpectatorNotRegistered() }
        val team = teamRepository.findById(teamId).orElseThrow { TeamNotRegisteredException() }

        spectator.markAsFavourite(team)

        spectatorRepository.save(spectator)

        return if (spectator.hasFavouriteTeam()) {
            TeamDTO.fromModel(spectator.favouriteTeam!!)
        } else {
            null
        }
    }

    @Transactional
    fun fansFrom(matchId: Long): List<Spectator> {
        val match = matchRepository.findById(matchId).orElseThrow { MatchDoNotExistsException() }

        return spectatorRepository.fansFrom(match)
    }

}

data class TicketDTO(val id: Long, val userId: Long, val matchId: Long, val home: String, val away: String, val matchStartTime: LocalDateTime) {
    companion object {
        fun fromModel(spectatorId: Long, ticket: Ticket): TicketDTO {
            return TicketDTO(ticket.id!!, spectatorId, ticket.match.id!!, ticket.match.home.name, ticket.match.away.name, ticket.match.matchStartTime)
        }
    }
}