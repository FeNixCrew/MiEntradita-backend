package ar.edu.unq.mientradita.model

import java.time.LocalDateTime

class Spectator(val surname: String, val username: String, val name: String, val email: String, val dni: Int,val password: String) {

    private val favoriteTeams = mutableListOf<Team>()
    private val tickets = mutableListOf<Ticket>()

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
