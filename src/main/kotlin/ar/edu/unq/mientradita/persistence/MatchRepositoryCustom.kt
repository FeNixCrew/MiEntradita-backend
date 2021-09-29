package ar.edu.unq.mientradita.persistence

import ar.edu.unq.mientradita.model.Match

interface MatchRepositoryCustom {
    fun searchNextMatchsBy(partialTeamName: String): List<Match>
}