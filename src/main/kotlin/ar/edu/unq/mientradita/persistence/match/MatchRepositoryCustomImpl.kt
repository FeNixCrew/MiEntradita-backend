package ar.edu.unq.mientradita.persistence.match

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.Team
import ar.edu.unq.mientradita.model.Ticket
import ar.edu.unq.mientradita.model.user.Spectator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Join
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

@Repository
class MatchRepositoryCustomImpl: MatchRepositoryCustom {

    @Autowired
    private lateinit var em: EntityManager

    override fun searchNextMatchsBy(partialTeamName: String, isFinished: Boolean?, aDate: LocalDateTime): List<Match> {
        val cb = em.criteriaBuilder
        val cq: CriteriaQuery<Match> = cb.createQuery(Match::class.java)
        val match: Root<Match> = cq.from(Match::class.java)

        val matchWithHomeTeam = cb.like(cb.upper(match.get<Team>("home").get("name")), "%${partialTeamName.toUpperCase()}%")
        val matchWithAwayTeam = cb.like(cb.upper(match.get<Team>("away").get("name")), "%${partialTeamName.toUpperCase()}%")

        val predicates: MutableList<Predicate> = ArrayList()

        val matchWithAnyTeam = cb.or(matchWithHomeTeam, matchWithAwayTeam)
        predicates.add(matchWithAnyTeam)

        if(isFinished!=null) {
            val expectedTime = aDate.minusMinutes(90)
            if (isFinished) {
                val finished = cb.lessThan(match.get("matchStartTime"), expectedTime)
                predicates.add(finished)
            } else {
                val notFinished = cb.greaterThanOrEqualTo(match.get("matchStartTime"), expectedTime)
                predicates.add(notFinished)
            }
        }

        cq.where(*predicates.toTypedArray())
        cq.orderBy(cb.asc(match.get<LocalDateTime>("matchStartTime")))

        return em.createQuery(cq).resultList
    }

    override fun matchFromTeamBetweenDate(team: String, wantedStartTime: LocalDateTime): Optional<Match> {
        val cb = em.criteriaBuilder
        val cq: CriteriaQuery<Match> = cb.createQuery(Match::class.java)
        val match: Root<Match> = cq.from(Match::class.java)

        val matchWithHomeTeam = cb.like(cb.upper(match.get<Team>("home").get("name")), team.toUpperCase())
        val matchWithAwayTeam = cb.like(cb.upper(match.get<Team>("away").get("name")), team.toUpperCase())

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

    override fun matchsOf(actualTime: LocalDateTime): List<Match> {
        val cb = em.criteriaBuilder
        val cq: CriteriaQuery<Match> = cb.createQuery(Match::class.java)
        val match: Root<Match> = cq.from(Match::class.java)

        val playAfter = cb.greaterThanOrEqualTo(match.get("matchStartTime"), actualTime)
        val playBefore = cb.lessThan(match.get("matchStartTime"), actualTime.plusDays(1))

        val condition = cb.and(playAfter, playBefore)
        cq.where(condition)
        cq.orderBy(cb.asc(match.get<LocalDateTime>("matchStartTime")))

        return em.createQuery(cq).resultList
    }

    override fun rememberOf(actualTime: LocalDateTime): List<MailAndMatch> {
        val cb = em.criteriaBuilder
        val cq: CriteriaQuery<MailAndMatch> = cb.createQuery(MailAndMatch::class.java)
        val spectator: Root<Spectator> = cq.from(Spectator::class.java)
        val spectatorAndTicket: Join<Spectator, Ticket> = spectator.join("tickets")

        val playAfter = cb.greaterThanOrEqualTo(spectatorAndTicket.get<Match>("match").get("matchStartTime"), actualTime.plusHours(24))
        val playBefore = cb.lessThan(spectatorAndTicket.get<Match>("match").get("matchStartTime"), actualTime.plusHours(48))

        val condition = cb.and(playAfter, playBefore)
        cq.where(condition)

        cq.multiselect(
            spectator.get<String>("email"),
            spectatorAndTicket.get<Match>("match")
        )

        return em.createQuery(cq).resultList
    }

    override fun getSpectatorsAttendance(match: Match): List<SpectatorAttendance> {
        val cb = em.criteriaBuilder
        val cq: CriteriaQuery<SpectatorAttendance> = cb.createQuery(SpectatorAttendance::class.java)
        val spectator: Root<Spectator> = cq.from(Spectator::class.java)
        val spectatorAndTicket: Join<Spectator, Ticket> = spectator.join("tickets")

        val isMatch = cb.equal(spectatorAndTicket.get<Match>("match").get<Long>("id"), match.id)
        cq.where(isMatch)

        cq.orderBy(cb.desc(spectator.get<Long>("dni")))

        cq.multiselect(
            spectator.get<Long>("id"),
            spectator.get<Int>("dni"),
            spectator.get<String>("name"),
            spectator.get<String>("surname"),
            spectatorAndTicket.get<LocalDateTime?>("presentTime")
        )

        return em.createQuery(cq).resultList
    }


}


data class MailAndMatch(val mail:String, val match: Match)

data class SpectatorAttendance(
    val id: Long,
    val dni: Int,
    val name: String,
    val surname: String,
    val presentTime: LocalDateTime?,
)