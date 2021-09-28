package ar.edu.unq.mientradita.persistence

import ar.edu.unq.mientradita.model.user.User
import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserRepository: CrudRepository<User, Long> {
    fun findByUsernameAndPassword(username: String, password: String): Optional<User>
    fun findByUsername(username: String): Optional<User>
}