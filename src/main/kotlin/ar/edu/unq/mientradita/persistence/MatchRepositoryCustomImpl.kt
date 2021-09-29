package ar.edu.unq.mientradita.persistence

import ar.edu.unq.mientradita.model.Match
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import javax.persistence.EntityManager
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

@Repository
class MatchRepositoryCustomImpl: MatchRepositoryCustom {

    @Autowired
    private lateinit var em: EntityManager

    override fun searchNextMatchsBy(partialTeamName: String, aDate: LocalDateTime): List<Match> {
        val cb = em.criteriaBuilder
        val cq: CriteriaQuery<Match> = cb.createQuery(Match::class.java)
        val match: Root<Match> = cq.from(Match::class.java)

        val matchWithHomeTeam = cb.like(cb.upper(match.get("home")), "%${partialTeamName.toUpperCase()}%")
        val matchWithAwayTeam = cb.like(cb.upper(match.get("away")), "%${partialTeamName.toUpperCase()}%")

        val isntPlayed = cb.greaterThanOrEqualTo(match.get("matchStartTime"), aDate)
        val matchWithAnyTeam = cb.or(matchWithHomeTeam, matchWithAwayTeam)

        val condition = cb.and(isntPlayed, matchWithAnyTeam)

        cq.where(condition)

        return em.createQuery(cq).resultList
    }

}