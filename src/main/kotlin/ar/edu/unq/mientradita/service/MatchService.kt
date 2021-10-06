package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.exception.MatchAlredyExists
import ar.edu.unq.mientradita.model.exception.MatchDoNotExistsException
import ar.edu.unq.mientradita.model.exception.SpectatorNotRegistered
import ar.edu.unq.mientradita.persistence.MatchRepository
import ar.edu.unq.mientradita.persistence.SpectatorRepository
import ar.edu.unq.mientradita.webservice.CreateMatchRequest
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
    fun createMatch(createMatchRequest: CreateMatchRequest): MatchDTO {
        val maybeMatch = matchRepository.findByHomeAndAwayAndMatchStartTime(createMatchRequest.home, createMatchRequest.away, createMatchRequest.matchStartTime)
        if(maybeMatch.isPresent){
            throw MatchAlredyExists()
        }
        val match = createMatchRequest.toModel()
        matchRepository.save(match)

        return MatchDTO.fromModel(match)
    }


    @Transactional
    fun comeIn(spectatorId: Long, matchId: Long, attendTime: LocalDateTime = LocalDateTime.now()): String {
        val match = matchRepository.findById(matchId).orElseThrow { MatchDoNotExistsException() }
        val spectator = spectatorRepository.findById(spectatorId).orElseThrow { SpectatorNotRegistered() }
        val ticket = spectator.findTicketFrom(match)

        match.comeIn(ticket, attendTime)

        return "Bienvenido ${spectator.username} al partido de ${match.home} vs ${match.away}"
    }

    @Transactional
    fun searchNextMatchsByPartialName(partialTeamName: String, aDate: LocalDateTime = LocalDateTime.now()): List<MatchDTO> {
        return matchRepository.searchNextMatchsBy(partialTeamName, aDate).map { MatchDTO.fromModel(it) }
    }

    @Transactional
    fun clearDataSet() {
        spectatorRepository.deleteAll()
        matchRepository.deleteAll()
    }

    @Transactional
    fun getTeams(): List<TeamDTO> {
        return matchRepository.findAll().flatMap{ match -> listOf(TeamDTO(match.home), TeamDTO(match.away))}.toSet().toList()
    }

}

data class MatchDTO(
        val id: Long,
        val home: String,
        val away: String,
        val ticketPrice: Double,
        val matchStartTime: LocalDateTime
) {
    companion object {
        fun fromModel(match: Match): MatchDTO {
            return MatchDTO(match.id!!, match.home, match.away, match.ticketPrice, match.matchStartTime)
        }
    }
}


data class TeamDTO(val name: String)