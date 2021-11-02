package ar.edu.unq.mientradita.persistence.spectator

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.Team
import ar.edu.unq.mientradita.model.user.Spectator
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SpectatorRepositoryCustom {
    fun fansFrom(match: Match): List<Spectator>
    fun nextMatchesOf(team: Team, aDateTime: LocalDateTime): List<Match>
}