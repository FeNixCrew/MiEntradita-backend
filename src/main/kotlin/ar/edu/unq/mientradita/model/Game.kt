package ar.edu.unq.mientradita.model

import ar.edu.unq.mientradita.model.exception.DifferentGameException
import ar.edu.unq.mientradita.model.exception.InvalidClosingTimeException
import ar.edu.unq.mientradita.model.exception.InvalidOpeningTimeException
import java.time.LocalDateTime

class Game(val local: Team, val visitant: Team, val gameStartTime: LocalDateTime, var availableTickets: Int) {

    fun reserveTicket(fan: Fan, reserveTicketTime: LocalDateTime) {
        val newTicket = Ticket(fan, this, reserveTicketTime)
        fan.addTicket(newTicket)
        availableTickets -= 1
    }

    fun comeIn(ticket: Ticket, attendDate: LocalDateTime) {
        checkIfIsTheSameGame(ticket.game)
        checkIfCanComeIn(attendDate)

        ticket.markAsPresent()
    }

    fun isEquals(game: Game) = this.local.isEquals(game.local) && this.visitant.isEquals(game.visitant)

    private fun checkIfIsTheSameGame(game: Game) {
        if (!this.isEquals(game)) throw DifferentGameException()
    }

    private fun checkIfCanComeIn(attendDate: LocalDateTime) {
        if (attendDate < openingTime()) throw InvalidOpeningTimeException()
        if (attendDate > closingTime()) throw InvalidClosingTimeException()
    }
    private fun openingTime() = gameStartTime.minusHours(3)
    private fun closingTime() = gameStartTime.plusMinutes(90)

}
