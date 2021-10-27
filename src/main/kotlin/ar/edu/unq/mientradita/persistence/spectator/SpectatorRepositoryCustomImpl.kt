package ar.edu.unq.mientradita.persistence.spectator

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.Team
import ar.edu.unq.mientradita.model.user.Spectator
import org.springframework.beans.factory.annotation.Autowired
import javax.persistence.EntityManager
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root

class SpectatorRepositoryCustomImpl: SpectatorRepositoryCustom {

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
}