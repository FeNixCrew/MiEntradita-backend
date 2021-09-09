package ar.edu.unq.mientradita.model.builders

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.Team
import java.time.LocalDateTime

class MatchBuilder {

    private var home: Team = TeamBuilder().build()
    private var away: Team = TeamBuilder().build()
    private var availableTickets: Int = 0
    private var matchStartTime: LocalDateTime = LocalDateTime.now()

    fun build(): Match {
        return Match(home, away, matchStartTime, availableTickets)
    }

    fun withHome(home: Team): MatchBuilder {
        this.home = home
        return this
    }

    fun withAway(away: Team): MatchBuilder {
        this.away = away
        return this
    }

    fun withMatchStart(matchStartTime: LocalDateTime): MatchBuilder {
        this.matchStartTime = matchStartTime
        return this
    }

    fun withAvailableTickets(availableTickets: Int): MatchBuilder {
        this.availableTickets = availableTickets
        return this
    }
}