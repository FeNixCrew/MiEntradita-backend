package ar.edu.unq.mientradita.service.impl

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.persistence.MatchRepository
import ar.edu.unq.mientradita.persistence.TeamRepository
import ar.edu.unq.mientradita.service.MatchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MatchServiceImpl: MatchService {

    @Autowired
    private lateinit var matchRepository: MatchRepository

    @Autowired
    private lateinit var teamRepository: TeamRepository

    override fun createMatch(homeId: Long, awayId: Long, ticketPrice: Double, matchStartTime: LocalDateTime): Match {
        val home = teamRepository.findById(homeId).get()
        val away = teamRepository.findById(awayId).get()
        val availableTickets: Int = home.stadium.capacity / 30

        val match = Match(home, away, matchStartTime, availableTickets)

        return matchRepository.save(match)
    }

    override fun findMatchBy(id: Long): Match {
        return matchRepository.findById(id).get()
    }
}