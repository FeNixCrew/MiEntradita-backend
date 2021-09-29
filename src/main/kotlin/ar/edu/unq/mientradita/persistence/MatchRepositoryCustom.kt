package ar.edu.unq.mientradita.persistence

import ar.edu.unq.mientradita.model.Match

interface MatchRepositoryCustom {
    fun findNextMatchsFrom(teamName: String): List<Match>
}