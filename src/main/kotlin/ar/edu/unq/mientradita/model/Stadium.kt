package ar.edu.unq.mientradita.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Stadium(
        val name: String,
        val latitude: Double,
        val longitude: Double,
        val capacity: Int) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun isEquals(stadium: Stadium) = this.name == stadium.name
}
