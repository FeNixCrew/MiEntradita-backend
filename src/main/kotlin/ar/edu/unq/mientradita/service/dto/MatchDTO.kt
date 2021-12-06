package ar.edu.unq.mientradita.service.dto

import ar.edu.unq.mientradita.model.Match
import java.time.LocalDateTime

data class MatchDTO(
    val id: Long,
    val home: String,
    val away: String,
    val ticketPrice: Float,
    val matchStartTime: LocalDateTime,
    val stadium: String,
    val isReserved: Boolean?,
    val capacitySupported: Int,
    val availableTickets: Int,
    val percentageOfCapacityAllowed: Int
) {
    companion object {
        fun fromModel(match: Match, isReserved: Boolean? = null): MatchDTO {
            return MatchDTO(
                match.id!!,
                match.home.name,
                match.away.name,
                match.ticketPrice,
                match.matchStartTime,
                match.stadium().name,
                isReserved,
                match.maximumCapacity(),
                match.numberOfTicketsAvailable(),
                match.admittedPercentage
            )
        }
    }
}