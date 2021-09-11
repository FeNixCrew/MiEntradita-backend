package ar.edu.unq.mientradita.model

@Deprecated("sobrediseniado")
class Team(
    val name: String,
    val stadium: Stadium) {

    fun isEquals(team: Team) = team.name.equals(this.name, ignoreCase = true)
}
