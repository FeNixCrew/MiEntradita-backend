package ar.edu.unq.mientradita.model.user

import javax.persistence.*

@Entity
@Inheritance(strategy= InheritanceType.JOINED)
open class MiEntraditaUser(
    @Column(unique = true)
    open val username: String,
    open val password: String,
    @Column(unique = true)
    open val email:String,
    open val role: Role) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
}