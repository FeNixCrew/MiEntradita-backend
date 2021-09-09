package ar.edu.unq.mientradita.model

import javax.persistence.*

@Entity
class Team(
    val name: String,
    @OneToOne(fetch = FetchType.LAZY)
    val stadium: Stadium) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun isEquals(team: Team) = team.name.equals(this.name, ignoreCase = true)
}
