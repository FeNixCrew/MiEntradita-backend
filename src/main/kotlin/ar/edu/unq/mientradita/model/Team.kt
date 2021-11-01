package ar.edu.unq.mientradita.model

import javax.persistence.*

@Entity
class Team(
    @Column(unique = true)
    val name: String,
    val knowName: String,
    val stadium: String,
    val stadiumCapacity: Int) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun isEquals(team: Team) = this.name == team.name
}
