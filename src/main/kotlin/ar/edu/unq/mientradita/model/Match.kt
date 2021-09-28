package ar.edu.unq.mientradita.model

import ar.edu.unq.mientradita.model.exception.DifferentGameException
import ar.edu.unq.mientradita.model.exception.InvalidClosingTimeException
import ar.edu.unq.mientradita.model.exception.InvalidOpeningTimeException
import ar.edu.unq.mientradita.model.user.Spectator
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Match(
    val home: String,
    val away: String,
    val matchStartTime: LocalDateTime,
    ) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun reserveTicket(spectator: Spectator, reserveTicketTime: LocalDateTime) {
        val newTicket = Ticket(this, reserveTicketTime)
        spectator.addTicket(newTicket)
    }

    fun comeIn(ticket: Ticket, attendDate: LocalDateTime) {
        checkIfIsTheSameMatch(ticket.match)
        checkIfCanComeIn(attendDate)

        ticket.markAsPresent(attendDate)
    }

    fun isEquals(match: Match) = this.home.equals(match.home, ignoreCase = true) && this.away.equals(match.away, ignoreCase = true)

    fun isBeforeMatchEnd(aTime: LocalDateTime) = closingTime() >= aTime

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
