package ar.edu.unq.mientradita.persistence

import ar.edu.unq.mientradita.model.Match
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MatchRepository: CrudRepository<Match, Long>
