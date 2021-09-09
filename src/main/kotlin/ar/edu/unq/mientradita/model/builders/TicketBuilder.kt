package ar.edu.unq.mientradita.model.builders

import ar.edu.unq.mientradita.model.Attend
import ar.edu.unq.mientradita.model.Fan
import ar.edu.unq.mientradita.model.Game
import ar.edu.unq.mientradita.model.Ticket
import java.time.LocalDateTime

class TicketBuilder {
    private var game: Game = GameBuilder().build()
    private var reservation: LocalDateTime = LocalDateTime.now()
    private var state: Attend = Attend.PENDING
    private var fan: Fan = FanBuilder().build()

    fun build(): Ticket {
        return Ticket(fan, game, reservation, state)
    }

    fun withGame(game: Game): TicketBuilder {
        this.game = game
        return this
    }

    fun withReservation(reservation: LocalDateTime): TicketBuilder {
        this.reservation = reservation
        return this
    }

    fun withState(game: Game): TicketBuilder {
        this.game = game
        return this
    }

    fun withFan(fan: Fan): TicketBuilder {
        this.fan = fan
        return this
    }
}