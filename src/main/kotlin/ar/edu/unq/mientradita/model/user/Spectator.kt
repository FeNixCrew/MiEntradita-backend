package ar.edu.unq.mientradita.model.user

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.Ticket
import ar.edu.unq.mientradita.model.exception.TicketFromMatchNotFoundException
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@PrimaryKeyJoinColumn(name="id")
class Spectator(
    val name: String,
    val surname: String,
    username: String,
    email: String,
    val dni: Int,
    password: String
): User(username, password, email, Role.USER) {

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val tickets = mutableListOf<Ticket>()

    fun reserveATicketFor(match: Match, reserveTime: LocalDateTime = LocalDateTime.now()) {
        match.reserveTicket(this, reserveTime)
    }

    fun addTicket(aTicket: Ticket) {
        tickets.add(aTicket)
    }

    fun findTicketFrom(match: Match) =
        tickets.find { ticket -> ticket.match.isEquals(match)} ?: throw TicketFromMatchNotFoundException(this, match)

    fun fullname() = "$name $surname"
}
