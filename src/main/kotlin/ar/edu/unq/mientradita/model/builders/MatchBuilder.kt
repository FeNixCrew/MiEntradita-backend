package ar.edu.unq.mientradita.model.builders

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.Team
import java.time.LocalDateTime

class MatchBuilder {

    private var home: Team = TeamBuilder().build()
    private var away: Team = TeamBuilder().build()
    private var matchStartTime: LocalDateTime = LocalDateTime.now()
    private var ticketPrice: Double = 0.0

    fun build(): Match {
        return Match(home, away, matchStartTime, ticketPrice)
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

    fun withPrice(aPrice: Double): MatchBuilder {
        this.ticketPrice = aPrice
        return this
    }
}