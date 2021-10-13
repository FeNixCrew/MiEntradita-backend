package ar.edu.unq.mientradita.persistence

import ar.edu.unq.mientradita.model.Team
import org.springframework.data.repository.CrudRepository
import java.util.*

interface TeamRepository: CrudRepository<Team, Long> {
    fun findByName(teamName: String): Optional<Team>
}