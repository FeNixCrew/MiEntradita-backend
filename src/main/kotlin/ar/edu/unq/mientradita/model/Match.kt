package ar.edu.unq.mientradita.model

import ar.edu.unq.mientradita.model.exception.DifferentGameException
import ar.edu.unq.mientradita.model.exception.InvalidClosingTimeException
import ar.edu.unq.mientradita.model.exception.InvalidOpeningTimeException
import ar.edu.unq.mientradita.model.exception.TicketsOffException
import java.time.LocalDateTime

class Match(val home: Team, val away: Team, val matchStartTime: LocalDateTime, var availableTickets: Int) {

    fun reserveTicket(spectator: Spectator, reserveTicketTime: LocalDateTime) {
        if(availableTickets == 0) throw TicketsOffException()
        val newTicket = Ticket(spectator, this, reserveTicketTime)
        spectator.addTicket(newTicket)
        availableTickets -= 1
    }

    fun comeIn(ticket: Ticket, attendDate: LocalDateTime) {
        checkIfIsTheSameMatch(ticket.match)
        checkIfCanComeIn(attendDate)

        ticket.markAsPresent()
    }

    fun isEquals(match: Match) = this.home.isEquals(match.home) && this.away.isEquals(match.away)

    private fun checkIfIsTheSameMatch(match: Match) {
        if (!this.isEquals(match)) throw DifferentGameException()
    }

    private fun checkIfCanComeIn(attendDate: LocalDateTime) {
        if (attendDate < openingTime()) throw InvalidOpeningTimeException()
        if (attendDate > closingTime()) throw InvalidClosingTimeException()
    }
    private fun openingTime() = matchStartTime.minusHours(3)
    private fun closingTime() = matchStartTime.plusMinutes(90)

}
