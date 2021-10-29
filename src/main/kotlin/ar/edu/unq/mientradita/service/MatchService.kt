package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.Team
import ar.edu.unq.mientradita.model.exception.*
import ar.edu.unq.mientradita.persistence.match.MatchRepository
import ar.edu.unq.mientradita.persistence.spectator.SpectatorRepository
import ar.edu.unq.mientradita.persistence.TeamRepository
import ar.edu.unq.mientradita.webservice.controllers.CreateMatchRequest
import ar.edu.unq.mientradita.webservice.config.security.JWTUtil
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

    @Autowired
    private lateinit var jwtUtil: JWTUtil

    @Transactional
    fun createMatch(createMatchRequest: CreateMatchRequest, actualTime: LocalDateTime = LocalDateTime.now()): MatchDTO {
        val home = teamRepository.findByName(createMatchRequest.home).orElseThrow { TeamNotFoundException(createMatchRequest.home) }
        val away = teamRepository.findByName(createMatchRequest.away).orElseThrow { TeamNotFoundException(createMatchRequest.away) }

        checkIsntSameTeam(createMatchRequest)
        checkValidTime(createMatchRequest.matchStartTime, actualTime)
        checkIfCanPlay(home, away, createMatchRequest)

        val match = Match(home, away, createMatchRequest.matchStartTime, createMatchRequest.ticketPrice)
        if(createMatchRequest.admittedPercentage != null) match.admittedPercentage = createMatchRequest.admittedPercentage

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
    fun searchNextMatchsByPartialName(
        partialTeamName: String,
        token: String? = null,
        aDate: LocalDateTime = LocalDateTime.now()): List<MatchDTO> {
        val matchs = matchRepository.searchNextMatchsBy(partialTeamName, aDate)

        return if (isAnUser(token)) {
            matchsByUser(token!!, matchs)
        } else {
            matchs.map { MatchDTO.fromModel(it) }
        }
    }

    @Transactional
    fun getMatchDetails(matchId: Long): MatchDTO {
        val match = matchRepository.findById(matchId).orElseThrow { MatchDoNotExistsException() }
        return MatchDTO.fromModel(match)
    }

    @Transactional
    fun todayMatchs(actualTime: LocalDateTime = LocalDateTime.now()): List<MatchDTO> {
        return matchRepository.matchsOf(actualTime).map { MatchDTO.fromModel(it) }
    }

    @Transactional
    fun matchs(): List<MatchDTO> {
        return matchRepository.findAll().map { MatchDTO.fromModel(it) }
    }

    @Transactional
    fun clearDataSet() {
        spectatorRepository.deleteAll()
        matchRepository.deleteAll()
        teamRepository.deleteAll()
    }

    private fun matchsByUser(token: String, matchs: List<Match>): List<MatchDTO> {
        val spectator = spectatorRepository
            .findByUsername(jwtUtil.getUsername(token))
            .orElseThrow { SpectatorNotRegistered() }

        return matchs.map{ MatchDTO.fromModel(it, spectator.wasReserved(it)) }
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

    private fun isAnUser(token: String?) = token != null && jwtUtil.isRoleUser(token)
}

data class MatchDTO(
        val id: Long,
        val home: String,
        val away: String,
        val ticketPrice: Double,
        val matchStartTime: LocalDateTime,
        val stadium: String,
        val isReserved: Boolean?,
        val capacitySupported: Int,
        val availableTickets: Int
) {
    companion object {
        fun fromModel(match: Match, isReserved: Boolean? = null): MatchDTO {
            return MatchDTO(match.id!!, match.home.name, match.away.name, match.ticketPrice, match.matchStartTime, match.stadium(), isReserved, match.maximumCapacity(), match.numberOfTicketsAvailable())
        }
    }
}
