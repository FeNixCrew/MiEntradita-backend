package ar.edu.unq.mientradita.model

import ar.edu.unq.mientradita.model.exception.TicketFromMatchNotFoundException
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Spectator(
    val name: String,
    val surname: String,
    val username: String,
    val email: String,
    val dni: Int,
    val password: String
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val tickets = mutableListOf<Ticket>()

    fun haveTickets(): Boolean = tickets.isNotEmpty()

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
