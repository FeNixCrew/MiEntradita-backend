package ar.edu.unq.mientradita.model.builders

import ar.edu.unq.mientradita.model.Stadium
import ar.edu.unq.mientradita.model.Team

class TeamBuilder {
    private var name: String = ""
    private var stadium: Stadium = StadiumBuilder().build()

    fun build(): Team {
        return Team(name, stadium)
    }

    fun withName(name: String): TeamBuilder {
        this.name = name
        return this
    }

    fun withStadium(stadium: Stadium): TeamBuilder {
        this.stadium = stadium
        return this
    }


}
