package ar.edu.unq.mientradita.service.impl

import ar.edu.unq.mientradita.model.Spectator
import ar.edu.unq.mientradita.persistence.MatchRepository
import ar.edu.unq.mientradita.persistence.SpectatorRepository
import ar.edu.unq.mientradita.persistence.TicketRepository
import ar.edu.unq.mientradita.service.MatchService
import ar.edu.unq.mientradita.service.SpectatorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class SpectatorServiceImpl: SpectatorService {

    @Autowired
    private lateinit var spectatorRepository: SpectatorRepository

    @Autowired
    private lateinit var matchService: MatchService

    @Autowired
    private lateinit var matchRepository: MatchRepository


    override fun createSpectator(name: String, surname: String, username: String, password: String, email: String, dni: Int): Spectator {
        val spectator = Spectator(surname,username,name,email,dni,password)
        return spectatorRepository.save(spectator)
    }

    override fun findSpectatorById(id: Long): Spectator {
        return spectatorRepository.findById(id).get()
    }

    override fun reserveTicket(matchId: Long, spectatorId: Long, reserveTicketIme: LocalDateTime): Spectator {
        val match = matchRepository.findById(matchId).get()
        val spectator = spectatorRepository.findById(spectatorId).get()

        spectator.reserveATicketFor(match, reserveTicketIme)

        matchRepository.save(match)
        return spectatorRepository.save(spectator)
    }
}