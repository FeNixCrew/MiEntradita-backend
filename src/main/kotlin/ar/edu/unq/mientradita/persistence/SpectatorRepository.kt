package ar.edu.unq.mientradita.persistence

import ar.edu.unq.mientradita.model.user.Spectator
import ar.edu.unq.mientradita.model.user.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SpectatorRepository: CrudRepository<Spectator,Long> {
    fun findByDni(dni: Int): Optional<Spectator>
    fun findByUsername(username: String): Optional<Spectator>
}