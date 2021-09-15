package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.exception.MatchDoNotExistsException
import ar.edu.unq.mientradita.model.exception.SpectatorNotRegistered
import ar.edu.unq.mientradita.persistence.MatchRepository
import ar.edu.unq.mientradita.persistence.SpectatorRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class MatchService {

    @Autowired
    private lateinit var matchRepository: MatchRepository

    @Autowired
    private lateinit var spectatorRepository: SpectatorRepository

    @Transactional
    fun createMatch(home: String, away: String, ticketPrice: Double, matchStartTime: LocalDateTime): Match {
        val match = Match(home, away, matchStartTime)

        return matchRepository.save(match)
    }

    @Transactional
    fun findMatchBy(id: Long): Match {
        return matchRepository.findById(id).get()
    }


    @Transactional
    fun comeIn(spectatorId: Long, matchId: Long, attendTime: LocalDateTime = LocalDateTime.now()): String {
        val match = matchRepository.findById(matchId).orElseThrow { MatchDoNotExistsException() }
        val spectator = spectatorRepository.findById(spectatorId).orElseThrow { SpectatorNotRegistered() }
        val ticket = spectator.findTicketFrom(match)

        match.comeIn(ticket, attendTime)

        return "Bienvenido ${spectator.username} al partido de ${match.home} vs ${match.away}"
    }

}