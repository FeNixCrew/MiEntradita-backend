package ar.edu.unq.mientradita.persistence.spectator

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.user.Spectator
import org.springframework.stereotype.Repository

@Repository
interface SpectatorRepositoryCustom {
    fun fansFrom(match: Match): List<Spectator>
}