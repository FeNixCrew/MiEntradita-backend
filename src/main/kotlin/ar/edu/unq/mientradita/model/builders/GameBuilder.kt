package ar.edu.unq.mientradita.model.builders

import ar.edu.unq.mientradita.model.Game
import ar.edu.unq.mientradita.model.Team
import java.time.LocalDateTime

class GameBuilder {

    private var local: Team = TeamBuilder().build()
    private var visitant: Team = TeamBuilder().build()
    private var availableTickets: Int = 0
    private var gameStartTime: LocalDateTime = LocalDateTime.now()

    fun build(): Game {
        return Game(local, visitant, gameStartTime, availableTickets)
    }

    fun withLocal(local: Team): GameBuilder {
        this.local = local
        return this
    }

    fun withVisitant(visitant: Team): GameBuilder {
        this.visitant = visitant
        return this
    }

    fun withGameStart(gameStartTime: LocalDateTime): GameBuilder {
        this.gameStartTime = gameStartTime
        return this
    }

    fun withAvailableTickets(availableTickets: Int): GameBuilder {
        this.availableTickets = availableTickets
        return this
    }
}