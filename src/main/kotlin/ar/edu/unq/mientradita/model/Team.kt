package ar.edu.unq.mientradita.model

class Team(val name: String) {

    fun isEquals(team: Team) = team.name == this.name
}
