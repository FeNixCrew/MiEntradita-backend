package ar.edu.unq.mientradita.persistence

import ar.edu.unq.mientradita.model.Match
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*
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
        cq.orderBy(cb.asc(match.get<LocalDateTime>("matchStartTime")))

        return em.createQuery(cq).resultList
    }

    override fun matchFromTeamBetweenDate(team: String, wantedStartTime: LocalDateTime): Optional<Match> {
        val cb = em.criteriaBuilder
        val cq: CriteriaQuery<Match> = cb.createQuery(Match::class.java)
        val match: Root<Match> = cq.from(Match::class.java)

        val matchWithHomeTeam = cb.like(cb.upper(match.get("home")), team.toUpperCase())
        val matchWithAwayTeam = cb.like(cb.upper(match.get("away")), team.toUpperCase())

        val playBefore = cb.between(match.get("matchStartTime"), wantedStartTime.minusHours(72), wantedStartTime.plusHours(72))
        val matchWithAnyTeam = cb.or(matchWithHomeTeam, matchWithAwayTeam)

        val condition = cb.and(matchWithAnyTeam, playBefore)

        cq.where(condition)

        val result = em.createQuery(cq).resultList
        return if(result.size > 0) {
            Optional.of(result[0])
        } else {
            Optional.empty()
        }
    }


}