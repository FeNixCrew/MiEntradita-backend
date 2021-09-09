package ar.edu.unq.mientradita.model

import java.time.LocalDateTime
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
class Spectator(val surname: String, val username: String, val name: String, val email: String, val dni: Int,val password: String) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    //TODO: revisar MANY TO MANY en checkpoint
    @Transient
    val favoriteTeams = mutableListOf<Team>()

    @OneToMany( fetch = FetchType.LAZY)
    val tickets = mutableListOf<Ticket>()

    fun haveAFavoriteTeam(): Boolean = favoriteTeams.isNotEmpty()
    fun myFavoriteTeams(): List<Team> = favoriteTeams

    fun addFavoriteTeam(team: Team){
        favoriteTeams.add(team)
    }

    fun myTickets(): List<Ticket> = tickets

    fun haveTickets(): Boolean = tickets.isNotEmpty()

    fun reserveATicketFor(match: Match){
        match.reserveTicket(this, LocalDateTime.now())
    }

    fun addTicket(aTicket: Ticket) {
        tickets.add(aTicket)
    }

}
