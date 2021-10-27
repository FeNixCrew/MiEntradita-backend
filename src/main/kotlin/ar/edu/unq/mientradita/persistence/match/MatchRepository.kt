package ar.edu.unq.mientradita.persistence.match

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.Team
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MatchRepository : MatchRepositoryCustom, CrudRepository<Match, Long> {
    fun findByHomeAndAway(home: Team, away: Team): Optional<Match>
}
