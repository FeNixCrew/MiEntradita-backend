package ar.edu.unq.mientradita.model.builders

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.Ticket
import java.time.LocalDateTime

class TicketBuilder {
    private var match: Match = MatchBuilder().build()
    private var reservation: LocalDateTime = LocalDateTime.now()

    fun build(): Ticket {
        return Ticket(match, reservation)
    }

    fun withGame(match: Match): TicketBuilder {
        this.match = match
        return this
    }

    fun withReservation(reservation: LocalDateTime): TicketBuilder {
        this.reservation = reservation
        return this
    }
}