package ar.edu.unq.mientradita.model

import ar.edu.unq.mientradita.model.exception.TicketFromMatchNotFoundException
import java.time.LocalDateTime
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
class Spectator(
    val surname: String,
    val username: String,
    val name: String,
    val email: String,
    val dni: Int,
    val password: String
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    //TODO: esta relacion al partido no le interesa quien lo tiene como favorito pero en la
    // DB seria MANY TO MANY
    @Transient
    val favoriteTeams = mutableListOf<Team>()

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val tickets = mutableListOf<Ticket>()

    fun haveAFavoriteTeam(): Boolean = favoriteTeams.isNotEmpty()
    fun myFavoriteTeams(): List<Team> = favoriteTeams

    fun addFavoriteTeam(team: Team) {
        favoriteTeams.add(team)
    }

    fun myTickets(): List<Ticket> = tickets

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
