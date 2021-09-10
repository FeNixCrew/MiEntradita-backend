package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.Spectator
import ar.edu.unq.mientradita.model.Ticket
import java.time.LocalDateTime

interface SpectatorService {

    fun createSpectator(name: String, surname: String, username: String, password: String, email: String, dni: Int): Spectator

    fun findSpectatorById(id: Long): Spectator

    fun reserveTicket(matchId: Long,spectatorId: Long, reserveTicketTime: LocalDateTime): Spectator

    fun findTicketFrom(spectatorId: Long, matchId: Long): Ticket
}