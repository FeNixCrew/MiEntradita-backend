package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.Team
import ar.edu.unq.mientradita.model.Ticket
import ar.edu.unq.mientradita.model.exception.MatchDoNotExistsException
import ar.edu.unq.mientradita.model.exception.SpectatorNotRegistered
import ar.edu.unq.mientradita.model.exception.TeamNotRegisteredException
import ar.edu.unq.mientradita.model.user.Spectator
import ar.edu.unq.mientradita.persistence.TeamRepository
import ar.edu.unq.mientradita.persistence.TicketRepository
import ar.edu.unq.mientradita.persistence.match.MatchRepository
import ar.edu.unq.mientradita.persistence.spectator.SpectatorRepository
import ar.edu.unq.mientradita.service.client.MercadoPagoClient
import ar.edu.unq.mientradita.service.dto.MatchDTO
import ar.edu.unq.mientradita.service.dto.TeamDTO
import ar.edu.unq.mientradita.service.dto.TicketDTO
import ar.edu.unq.mientradita.service.dto.SuccessPaymentRequest
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
    private lateinit var ticketRepository: TicketRepository

    @Autowired
    private lateinit var teamRepository: TeamRepository

    @Autowired
    private lateinit var mercadoPagoClient: MercadoPagoClient

    @Transactional
    fun reserveTicket(
        spectatorId: Long,
        matchId: Long,
        reserveTicketTime: LocalDateTime = LocalDateTime.now()
    ): TicketDTO {
        val match = matchRepository.findById(matchId).orElseThrow { MatchDoNotExistsException() }
        val spectator = spectatorRepository.findById(spectatorId).orElseThrow { SpectatorNotRegistered() }

        val newTicket: Ticket = createTicketWithMPLink(spectator, match, reserveTicketTime)

        ticketRepository.save(newTicket)
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

    @Transactional
    fun nextMatchesOfFavoriteTeam(
        spectatorId: Long,
        dateTime: LocalDateTime = LocalDateTime.now()
    ): List<MatchDTO>? {
        val spectator = spectatorRepository.findById(spectatorId).orElseThrow { SpectatorNotRegistered() }
        val favouriteTeam: Team? = spectator.favouriteTeam

        return if (favouriteTeam != null) {
            val matchs = spectatorRepository.nextMatchsFor(favouriteTeam.id!!, dateTime)
            matchs.map { MatchDTO.fromModel(it, spectator.wasReserved(it)) }
        } else {
            null
        }
    }

    @Transactional
    fun pendingTicketsPaymentFor(spectatorId: Long): List<TicketDTO> {
        return obtainSpectator(spectatorId)
            .pendingPaymentTickets()
            .map { TicketDTO.fromModel(spectatorId, it) }
    }

    @Transactional
    fun savePaymentFrom(successPaymentRequest: SuccessPaymentRequest) {
        val ticket = ticketRepository.findById(successPaymentRequest.ticketId).get()
        obtainSpectator(successPaymentRequest.spectatorId)
            .savePayedTicket(ticket, successPaymentRequest.paymentId)
    }

    fun obtainSpectator(spectatorId: Long): Spectator {
        return spectatorRepository.findById(spectatorId).orElseThrow { SpectatorNotRegistered() }
    }

    private fun createTicketWithMPLink(
        spectator: Spectator,
        match: Match,
        reserveTicketTime: LocalDateTime
    ): Ticket {
        val newTicket: Ticket = spectator.reserveATicketFor(match, reserveTicketTime)
        ticketRepository.save(newTicket)
        val linkMP: String = mercadoPagoClient.createLink(spectator, newTicket)
        newTicket.savePaymentLink(linkMP)
        return newTicket
    }
}
