package ar.edu.unq.mientradita.service.impl

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.Spectator
import ar.edu.unq.mientradita.persistence.MatchRepository
import ar.edu.unq.mientradita.persistence.SpectatorRepository
import ar.edu.unq.mientradita.persistence.TeamRepository
import ar.edu.unq.mientradita.persistence.TicketRepository
import ar.edu.unq.mientradita.service.MatchService
import ar.edu.unq.mientradita.service.SpectatorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class MatchServiceImpl : MatchService {

    @Autowired
    private lateinit var matchRepository: MatchRepository

    @Autowired
    private lateinit var teamRepository: TeamRepository

    @Autowired
    private lateinit var spectatorRepository: SpectatorRepository

    @Transactional
    override fun createMatch(homeId: Long, awayId: Long, ticketPrice: Double, matchStartTime: LocalDateTime): Match {
        val home = teamRepository.findById(homeId).get()
        val away = teamRepository.findById(awayId).get()
        val availableTickets: Int = home.stadium.capacity / 30

        val match = Match(home, away, matchStartTime, availableTickets)

        return matchRepository.save(match)
    }

    @Transactional
    override fun findMatchBy(id: Long): Match {
        return matchRepository.findById(id).get()
    }


    @Transactional
    override fun comeIn(matchId: Long, spectatorId: Long, attendTime: LocalDateTime) {
        val match = matchRepository.findById(matchId).get()
        val ticket = spectatorRepository.findById(spectatorId).get().findTicketFrom(match)

        match.comeIn(ticket, attendTime)
    }

}