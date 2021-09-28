package ar.edu.unq.mientradita.model.user

import javax.persistence.*

@Entity
@Inheritance(strategy= InheritanceType.JOINED)
open class User(
    open val username: String,
    open val password: String,
    open val email:String,
    open val role: Role) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
}