package ar.edu.unq.mientradita.model

import javax.persistence.*

@Entity
class Stadium(
    val knownName: String,
    val realName: String,
    val capacity: Int,
    @OneToOne(fetch = FetchType.LAZY)
    val location: Location) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}