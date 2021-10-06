package ar.edu.unq.mientradita.persistence

import ar.edu.unq.mientradita.model.Match
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface MatchRepository : MatchRepositoryCustom, CrudRepository<Match, Long> {
    fun findByHomeAndAway(home: String, away: String): Optional<Match>
}
