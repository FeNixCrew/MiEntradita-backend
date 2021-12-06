package ar.edu.unq.mientradita.model

import javax.persistence.*

@Entity
class Team(
    @Column(unique = true)
    val name: String,
    val knowName: String,
    @OneToOne(fetch=FetchType.EAGER)
    val stadium: Stadium) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun isEquals(team: Team) = this.name == team.name
}
