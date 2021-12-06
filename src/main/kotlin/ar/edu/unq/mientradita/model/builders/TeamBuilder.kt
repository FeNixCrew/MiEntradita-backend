package ar.edu.unq.mientradita.model.builders

import ar.edu.unq.mientradita.model.Stadium
import ar.edu.unq.mientradita.model.Team

class TeamBuilder {

    private var name: String = "a"
    private var knowName: String = "b"
    private var stadium: Stadium = StadiumBuilder().build()

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

    fun withStadium(stadium: Stadium): TeamBuilder {
        this.stadium = stadium
        return this
    }
}