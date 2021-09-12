package ar.edu.unq.mientradita.model

import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Deprecated( message = "sobre diseniado")
class Location(val location: String, val latitude: Double, val longitude: Double){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}
