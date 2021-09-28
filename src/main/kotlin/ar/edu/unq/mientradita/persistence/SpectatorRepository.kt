package ar.edu.unq.mientradita.persistence

import ar.edu.unq.mientradita.model.user.Spectator
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SpectatorRepository: CrudRepository<Spectator,Long> {
    fun findByUsernameAndPassword(username: String, password: String): Spectator?
    fun findByUsername(username: String): Spectator?
}