package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.Team
import ar.edu.unq.mientradita.model.exception.*
import ar.edu.unq.mientradita.persistence.MatchRepository
import ar.edu.unq.mientradita.persistence.SpectatorRepository
import ar.edu.unq.mientradita.persistence.TeamRepository
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

    @Autowired
    private lateinit var teamRepository: TeamRepository

    @Transactional
    fun createMatch(createMatchRequest: CreateMatchRequest, actualTime: LocalDateTime = LocalDateTime.now()): MatchDTO {
        val home = teamRepository.findByName(createMatchRequest.home).orElseThrow { TeamNotFoundException(createMatchRequest.home) }
        val away = teamRepository.findByName(createMatchRequest.away).orElseThrow { TeamNotFoundException(createMatchRequest.away) }

        checkIsntSameTeam(createMatchRequest)
        checkValidTime(createMatchRequest.matchStartTime, actualTime)
        checkIfCanPlay(home, away, createMatchRequest)

        val match = Match(home, away, createMatchRequest.matchStartTime, createMatchRequest.ticketPrice, createMatchRequest.stadium)
        matchRepository.save(match)

        return MatchDTO.fromModel(match)
    }

    @Transactional
    fun comeIn(spectatorId: Long, matchId: Long, attendTime: LocalDateTime = LocalDateTime.now()): String {
        val match = matchRepository.findById(matchId).orElseThrow { MatchDoNotExistsException() }
        val spectator = spectatorRepository.findById(spectatorId).orElseThrow { SpectatorNotRegistered() }
        val ticket = spectator.findTicketFrom(match)

        match.comeIn(ticket, attendTime)

        return "Bienvenido ${spectator.username} al partido de ${match.home.name} vs ${match.away.name}"
    }


    @Transactional
    fun searchNextMatchsByPartialName(partialTeamName: String, aDate: LocalDateTime = LocalDateTime.now()): List<MatchDTO> {
        return matchRepository.searchNextMatchsBy(partialTeamName, aDate).map { MatchDTO.fromModel(it) }
    }

    @Transactional
    fun getMatchDetails(matchId: Long): MatchDTO {
        val match = matchRepository.findById(matchId).orElseThrow { MatchDoNotExistsException() }
        return MatchDTO.fromModel(match)
    }

    @Transactional
    fun clearDataSet() {
        spectatorRepository.deleteAll()
        matchRepository.deleteAll()
        teamRepository.deleteAll()
    }

    @Transactional
    fun getTeams(): List<TeamDTO> {
        return teamRepository.findAll().map { team -> TeamDTO(team.name) }
    }

    private fun checkIsntSameTeam(createMatchRequest: CreateMatchRequest) {
        if (createMatchRequest.home == createMatchRequest.away) {
            throw TeamCannotPlayAgainstHimselfException()
        }
    }

    private fun checkIfCanPlay(home: Team, away: Team, createMatchRequest: CreateMatchRequest) {
        checkIfWasPlayed(home, away)
        checkIfCanPlay(createMatchRequest.home, createMatchRequest)
        checkIfCanPlay(createMatchRequest.away, createMatchRequest)
    }

    private fun checkIfCanPlay(team: String, createMatchRequest: CreateMatchRequest) {
        val maybeMatch = matchRepository.matchFromTeamBetweenDate(team, createMatchRequest.matchStartTime)
        if (maybeMatch.isPresent) {
            throw TeamNearlyPlayException(team, createMatchRequest.matchStartTime, maybeMatch.get().matchStartTime)
        }
    }

    private fun checkIfWasPlayed(home: Team, away: Team) {
        if (matchRepository.findByHomeAndAway(home, away).isPresent) {
            throw MatchAlreadyExists(home.name, away.name)
        }
    }

    private fun checkValidTime(matchStartTime: LocalDateTime, actualTime: LocalDateTime) {
        val comparingStartTime = withoutTime(matchStartTime)
        val comparingActualTime = withoutTime(actualTime)

        if(comparingStartTime <= comparingActualTime.plusDays(6)) {
            throw InvalidStartTimeException()
        }
    }

    private fun withoutTime(aTime: LocalDateTime): LocalDateTime {
        return aTime.withHour(0).withMinute(0).withSecond(0).withNano(0)
    }
}

data class MatchDTO(
        val id: Long,
        val home: String,
        val away: String,
        val ticketPrice: Double,
        val matchStartTime: LocalDateTime,
        val stadium: String
) {
    companion object {
        fun fromModel(match: Match): MatchDTO {
            return MatchDTO(match.id!!, match.home.name, match.away.name, match.ticketPrice, match.matchStartTime, match.stadium)
        }
    }
}


data class TeamDTO(val name: String)
