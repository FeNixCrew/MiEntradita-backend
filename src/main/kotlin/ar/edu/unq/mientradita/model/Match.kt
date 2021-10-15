package ar.edu.unq.mientradita.model

import ar.edu.unq.mientradita.model.exception.DifferentGameException
import ar.edu.unq.mientradita.model.exception.InvalidClosingTimeException
import ar.edu.unq.mientradita.model.exception.InvalidOpeningTimeException
import ar.edu.unq.mientradita.model.user.Spectator
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Match(
    @ManyToOne(fetch= FetchType.LAZY)
    val home: Team,
    @ManyToOne(fetch=FetchType.LAZY)
    val away: Team,
    val matchStartTime: LocalDateTime,
    val ticketPrice: Double
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

    fun isEquals(match: Match) = this.home.isEquals(match.home) && this.away.isEquals(match.away)

    fun stadium() = home.stadium

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
