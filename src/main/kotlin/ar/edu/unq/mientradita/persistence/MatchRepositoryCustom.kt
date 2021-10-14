package ar.edu.unq.mientradita.persistence

import ar.edu.unq.mientradita.model.Match
import java.time.LocalDateTime
import java.util.*

interface MatchRepositoryCustom {
    fun searchNextMatchsBy(partialTeamName: String, aDate: LocalDateTime): List<Match>
    fun matchFromTeamBetweenDate(team: String, wantedStartTime: LocalDateTime): Optional<Match>
    fun matchsOf(actualTime: LocalDateTime): List<Match>
}