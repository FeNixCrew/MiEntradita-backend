package ar.edu.unq.mientradita.persistence

import ar.edu.unq.mientradita.model.user.MiEntraditaUser
import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserRepository: CrudRepository<MiEntraditaUser, Long> {
    fun findByUsernameAndPassword(username: String, password: String): Optional<MiEntraditaUser>
    fun findByUsername(username: String): Optional<MiEntraditaUser>
    fun findByUsernameIgnoreCase(username: String): Optional<MiEntraditaUser>
    fun findByEmailIgnoreCase(username: String): Optional<MiEntraditaUser>
}