package ar.edu.unq.mientradita.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Team(
    val name: String,
    val knowName: String,
    val stadium: String) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun isEquals(team: Team) = this.name == team.name
}
