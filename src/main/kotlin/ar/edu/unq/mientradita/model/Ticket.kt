package ar.edu.unq.mientradita.model

import java.time.LocalDateTime

class Ticket(val fan: Fan,
             val game: Game,
             val reservation: LocalDateTime = LocalDateTime.now(),
             var state: Attend = Attend.PENDING,
             ) {

    fun markAsPresent() {
        state = Attend.PRESENT
    }

    fun markAsAbsent() {
        state = Attend.ABSENT
    }
}