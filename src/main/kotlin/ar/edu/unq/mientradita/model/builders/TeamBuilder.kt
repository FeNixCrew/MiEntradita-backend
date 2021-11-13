package ar.edu.unq.mientradita.model.builders

import ar.edu.unq.mientradita.model.Team

class TeamBuilder {

    private var name: String = "a"
    private var knowName: String = "b"
    private var stadium: String = "c"
    private var maximumCapacity = 0

    fun build(): Team {
        return Team(name, knowName, stadium, maximumCapacity)
    }

    fun withName(name: String): TeamBuilder {
        this.name = name
        return this
    }

    fun withKnowName(knowName: String): TeamBuilder {
        this.knowName = knowName
        return this
    }

    fun withMaximumCapacity(maximumCapacity: Int): TeamBuilder {
        this.maximumCapacity = maximumCapacity
        return this
    }

    fun withStadium(stadium: String): TeamBuilder {
        this.stadium = stadium
        return this
    }
}