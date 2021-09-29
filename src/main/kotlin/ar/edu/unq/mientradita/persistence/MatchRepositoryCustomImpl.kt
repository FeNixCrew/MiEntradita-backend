package ar.edu.unq.mientradita.persistence

import ar.edu.unq.mientradita.model.Match
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root

@Repository
class MatchRepositoryCustomImpl: MatchRepositoryCustom {

    @Autowired
    private lateinit var em: EntityManager

    override fun findNextMatchsFrom(teamName: String): List<Match> {
        val cb = em.criteriaBuilder
        val cq: CriteriaQuery<Match> = cb.createQuery(Match::class.java)
        val match: Root<Match> = cq.from(Match::class.java)

        val matchWithHomeTeam = cb.like(cb.upper(match.get("home")), "%${teamName.toUpperCase()}%")
        val matchWithAwayTeam = cb.like(cb.upper(match.get("away")), "%${teamName.toUpperCase()}%")

        cq.where(cb.or(matchWithHomeTeam, matchWithAwayTeam))

        return em.createQuery(cq).resultList
    }

}