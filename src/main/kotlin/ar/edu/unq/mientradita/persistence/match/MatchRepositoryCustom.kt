package ar.edu.unq.mientradita.persistence.match

import ar.edu.unq.mientradita.model.Match
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface MatchRepositoryCustom {
    fun searchNextMatchsBy(partialTeamName: String, aDate: LocalDateTime): List<Match>
    fun matchFromTeamBetweenDate(team: String, wantedStartTime: LocalDateTime): Optional<Match>
    fun matchsOf(actualTime: LocalDateTime): List<Match>
    fun rememberOf(actualTime: LocalDateTime): List<MailAndMatch>
}