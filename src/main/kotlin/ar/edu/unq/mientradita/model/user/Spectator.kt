package ar.edu.unq.mientradita.model.user

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.Team
import ar.edu.unq.mientradita.model.Ticket
import ar.edu.unq.mientradita.model.exception.AlreadyExistsException
import ar.edu.unq.mientradita.model.exception.BusinessException
import ar.edu.unq.mientradita.model.exception.TicketFromMatchNotFoundException
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@PrimaryKeyJoinColumn(name = "id")
class Spectator(
    val name: String,
    val surname: String,
    username: String,
    email: String,
    @Column(unique = true)
    val dni: Int,
    password: String
) : MiEntraditaUser(username, password, email, Role.ROLE_USER) {

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val tickets = mutableListOf<Ticket>()

    @ManyToOne(fetch = FetchType.EAGER)
    var favouriteTeam: Team? = null

    fun markAsFavourite(team: Team) {
        if (this.favouriteTeam != null && team.isEquals(favouriteTeam!!)) {
            this.favouriteTeam = null
        } else {
            this.favouriteTeam = team
        }
    }

    fun reserveATicketFor(match: Match, reserveTime: LocalDateTime = LocalDateTime.now()): Ticket {
        if (tickets.any { it.match.isEquals(match) }) throw AlreadyExistsException("Ya tienes una entrada para este partido")
        return match.reserveTicket(this, reserveTime)
    }

    fun addTicket(aTicket: Ticket): Ticket {
        tickets.add(aTicket)
        return aTicket
    }

    fun findTicketFrom(match: Match) =
        tickets.find { ticket -> ticket.match.isEquals(match) }
            ?: throw TicketFromMatchNotFoundException(this, match)

    fun pendingPaymentTickets(): List<Ticket> {
        return tickets.filterNot { it.isPaid() }
    }

    fun savePayedTicket(ticket: Ticket, paymentId: String) {
        try {
            tickets.find { it.isFrom(ticket.match) }!!.markAsPaid(paymentId)
        } catch (_: NullPointerException) {
            throw BusinessException("la entrada no pertenece al espectador")
        }
    }

    fun fullname() = "$name $surname"

    fun wasReserved(aMatch: Match) = tickets.any { ticket -> ticket.isFrom(aMatch) }

    fun hasFavouriteTeam() = favouriteTeam != null
}
