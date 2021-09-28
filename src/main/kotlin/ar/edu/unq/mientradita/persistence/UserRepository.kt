package ar.edu.unq.mientradita.persistence

import ar.edu.unq.mientradita.model.user.User
import org.springframework.data.repository.CrudRepository

interface UserRepository: CrudRepository<User, Long> {
}