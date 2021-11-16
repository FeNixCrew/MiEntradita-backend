package ar.edu.unq.mientradita.model

import ar.edu.unq.mientradita.model.exception.*
import ar.edu.unq.mientradita.model.user.Spectator
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Match(
    @ManyToOne(fetch= FetchType.EAGER)
    val home: Team,
    @ManyToOne(fetch=FetchType.EAGER)
    val away: Team,
    val matchStartTime: LocalDateTime,
    val ticketPrice: Float
    ) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var admittedPercentage = 100
        set(newPercentage) {
            checkValidPercentage(newPercentage)
            field = newPercentage
        }

    private var reservedTickets: Int  = 0

    fun numberOfTicketsAvailable() = this.maximumCapacity() - reservedTickets

    fun reserveTicket(spectator: Spectator, reserveTicketTime: LocalDateTime): Ticket {
        if(numberOfTicketsAvailable() > 0){
            val newTicket = Ticket(this, reserveTicketTime)
            reservedTickets += 1
            return spectator.addTicket(newTicket)
        } else {
            throw TicketsNotAvailablesException()
        }
    }

    fun comeIn(ticket: Ticket, attendDate: LocalDateTime) {
        checkIfIsTheSameMatch(ticket.match)
        checkIfCanComeIn(attendDate)

        ticket.markAsPresent(attendDate)
    }

    fun maximumCapacity() = home.stadium.capacity * admittedPercentage / 100

    fun isEquals(match: Match) = this.home.isEquals(match.home) && this.away.isEquals(match.away)

    fun stadium() = home.stadium

    fun isBeforeMatchEnd(aTime: LocalDateTime) = closingTime() >= aTime

    override fun toString(): String {
        return "${home.name} vs ${away.name}"
    }

    private fun checkIfIsTheSameMatch(match: Match) {
        if (!this.isEquals(match)) throw DifferentGameException()
    }

    private fun checkIfCanComeIn(attendDate: LocalDateTime) {
        if (attendDate < openingTime()) throw InvalidOpeningTimeException()
        if (attendDate > closingTime()) throw InvalidClosingTimeException()
    }
    private fun openingTime() = matchStartTime.minusHours(3)

    private fun closingTime() = matchStartTime.plusMinutes(90)

    private fun checkValidPercentage(newPercentage: Int) {
        if(newPercentage < 0 || newPercentage > 100) throw InvalidPercentageException()
    }
}
