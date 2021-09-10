package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.Match
import java.time.LocalDateTime

interface MatchService {

    fun createMatch(homeId: Long, awayId: Long, ticketPrice: Double, matchStartTime: LocalDateTime): Match

    fun findMatchBy(id: Long): Match
}