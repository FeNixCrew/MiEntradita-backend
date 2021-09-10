package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.Spectator
import org.springframework.stereotype.Service
import java.time.LocalDateTime

interface MatchService {

    fun createMatch(homeId: Long, awayId: Long, ticketPrice: Double, matchStartTime: LocalDateTime): Match

    fun findMatchBy(id: Long): Match

    fun reserveTicket(matchId: Long,spectatorId: Long, reserveTicketIme: LocalDateTime): Match

}