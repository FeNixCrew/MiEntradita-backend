package ar.edu.unq.mientradita.model.builders

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.Ticket
import java.time.LocalDateTime

class TicketBuilder {
    private var match: Match = MatchBuilder().build()
    private var reservation: LocalDateTime = LocalDateTime.now()
    private var price: Double = 0.0

    fun build(): Ticket {
        return Ticket(match, reservation, price)
    }

    fun withGame(match: Match): TicketBuilder {
        this.match = match
        return this
    }

    fun withReservation(reservation: LocalDateTime): TicketBuilder {
        this.reservation = reservation
        return this
    }

    fun withPrice(aPrice: Double): TicketBuilder {
        this.price = aPrice
        return this
    }
}