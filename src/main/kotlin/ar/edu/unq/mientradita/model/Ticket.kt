package ar.edu.unq.mientradita.model

import ar.edu.unq.mientradita.model.exception.OldTicketException
import java.time.LocalDateTime

class Ticket(val reservation: LocalDateTime,
             var state: StateOfTicket = StateOfTicket.PENDING,
             val game: Game) {

    fun makeAsPresent() {
        if (reservation < LocalDateTime.now()) {
            makeAsAbsent()
            throw OldTicketException(game)
        } else {
            state = StateOfTicket.PRESENT
        }
    }

    fun makeAsAbsent() {
        state = StateOfTicket.ABSENT
    }

    fun wasAttended() = state.wasAttended()
}