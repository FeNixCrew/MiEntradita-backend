package ar.edu.unq.mientradita.service.dto

import ar.edu.unq.mientradita.model.Ticket
import java.time.LocalDateTime

data class TicketDTO(val id: Long, val userId: Long, val matchId: Long, val home: String, val away: String, val matchStartTime: LocalDateTime) {
    companion object {
        fun fromModel(spectatorId: Long, ticket: Ticket): TicketDTO {
            return TicketDTO(
                ticket.id!!,
                spectatorId,
                ticket.match.id!!,
                ticket.match.home.name,
                ticket.match.away.name,
                ticket.match.matchStartTime
            )
        }
    }
}