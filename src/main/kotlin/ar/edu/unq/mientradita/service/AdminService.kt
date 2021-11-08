package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.exception.MatchDoNotExistsException
import ar.edu.unq.mientradita.persistence.TeamRepository
import ar.edu.unq.mientradita.persistence.match.MatchRepository
import ar.edu.unq.mientradita.persistence.spectator.SpectatorRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AdminService {

    @Autowired
    private lateinit var matchRepository: MatchRepository

    @Autowired
    private lateinit var spectatorRepository: SpectatorRepository

    @Autowired
    private lateinit var teamRepository: TeamRepository


    @Transactional
    fun getMatchInformation(matchId: Long): List<SpectatorInMatchInformation> {
        val match = matchRepository.findById(matchId).orElseThrow { MatchDoNotExistsException() }
        val spectators = spectatorRepository.findAll()

        val spectatorsWhoBoughtTicketsTo = spectators.filter {
            it.wasReserved(match)
        }

        return spectatorsWhoBoughtTicketsTo.map {
            val ticket = it.findTicketFrom(match)
            SpectatorInMatchInformation(
                    it.id!!,
                    it.dni,
                    it.name,
                    it.surname,
                    ticket.wasPresent(),
                    ticket.presentTime)
        }
    }

}

data class SpectatorInMatchInformation(
        val spectatorId: Long,
        val spectatorDni: Int,
        val spectatorName: String,
        val spectatorSurname: String,
        val isPresent: Boolean,
        val checkIn: LocalDateTime?
) {

}