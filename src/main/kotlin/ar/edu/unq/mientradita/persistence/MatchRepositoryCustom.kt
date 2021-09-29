package ar.edu.unq.mientradita.persistence

import ar.edu.unq.mientradita.model.Match
import java.time.LocalDateTime

interface MatchRepositoryCustom {
    fun searchNextMatchsBy(partialTeamName: String, aDate: LocalDateTime): List<Match>
}