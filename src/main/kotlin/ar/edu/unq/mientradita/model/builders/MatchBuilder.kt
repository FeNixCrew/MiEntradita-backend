package ar.edu.unq.mientradita.model.builders

import ar.edu.unq.mientradita.model.Match
import java.time.LocalDateTime

class MatchBuilder {

    private var home: String = "a"
    private var away: String = "b"
    private var matchStartTime: LocalDateTime = LocalDateTime.now()
    private var ticketPrice: Double = 0.0
    private var stadium: String = "c"

    fun build(): Match {
        return Match(home, away, matchStartTime, ticketPrice, stadium)
    }

    fun withHome(home: String): MatchBuilder {
        this.home = home
        return this
    }

    fun withAway(away: String): MatchBuilder {
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

    fun withStadium(aStadium: String): MatchBuilder {
        this.stadium = aStadium
        return this
    }
}