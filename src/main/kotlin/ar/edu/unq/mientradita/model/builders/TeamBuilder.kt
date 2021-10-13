package ar.edu.unq.mientradita.model.builders

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.Team
import java.time.LocalDateTime

class TeamBuilder {

    private var name: String = "a"
    private var knowName: String = "b"
    private var stadium: String = "c"

    fun build(): Team {
        return Team(name, knowName, stadium)
    }

    fun withName(name: String): TeamBuilder {
        this.name = name
        return this
    }

    fun withKnowName(knowName: String): TeamBuilder {
        this.knowName = knowName
        return this
    }

    fun withStadium(stadium: String): TeamBuilder {
        this.stadium = stadium
        return this
    }
}