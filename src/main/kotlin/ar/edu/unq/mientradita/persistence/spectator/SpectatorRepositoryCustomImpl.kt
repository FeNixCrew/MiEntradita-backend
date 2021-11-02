package ar.edu.unq.mientradita.persistence.spectator

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.Team
import ar.edu.unq.mientradita.model.user.Spectator
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import javax.persistence.EntityManager
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root

class SpectatorRepositoryCustomImpl : SpectatorRepositoryCustom {

    @Autowired
    private lateinit var em: EntityManager

    override fun fansFrom(match: Match): List<Spectator> {
        val cb = em.criteriaBuilder
        val cq: CriteriaQuery<Spectator> = cb.createQuery(Spectator::class.java)
        val spectator: Root<Spectator> = cq.from(Spectator::class.java)

        val hasFavouriteTeam = cb.isNotNull(spectator.get<Team?>("favouriteTeam"))
        val playHomeFavouriteTeam = cb.equal(spectator.get<Team>("favouriteTeam").get<Long>("id"), match.home.id)
        val playAwayFavouriteTeam = cb.equal(spectator.get<Team>("favouriteTeam").get<Long>("id"), match.away.id)
        val playFavouriteTeam = cb.or(playHomeFavouriteTeam, playAwayFavouriteTeam)

        val condition = cb.and(hasFavouriteTeam, playFavouriteTeam)

        cq.where(condition)
        return em.createQuery(cq).resultList
    }

    override fun nextMatchsFor(teamId: Long, aDateTime: LocalDateTime): List<Match> {
        val cb = em.criteriaBuilder
        val cq: CriteriaQuery<Match> = cb.createQuery(Match::class.java)
        val root: Root<Match> = cq.from(Match::class.java)

        val matchInHomeCondition = cb.equal(root.get<Team>("home").get<Long>("id"), teamId)
        val matchInAwayCondition = cb.equal(root.get<Team>("away").get<Long>("id"), teamId)

        val notPlayed = cb.greaterThanOrEqualTo(root.get("matchStartTime"), aDateTime)
        val homeOrAway = cb.or(matchInHomeCondition, matchInAwayCondition)

        val conditionToApply = cb.and(homeOrAway, notPlayed)

        cq.where(conditionToApply)
        cq.orderBy(cb.asc(root.get<LocalDateTime>("matchStartTime")))

        return em.createQuery(cq).resultList
    }
}