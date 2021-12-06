package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.exception.AlreadyExistsException
import ar.edu.unq.mientradita.model.exception.TeamNotFoundException
import ar.edu.unq.mientradita.persistence.StadiumRepository
import ar.edu.unq.mientradita.persistence.TeamRepository
import ar.edu.unq.mientradita.service.dto.CreateTeamRequest
import ar.edu.unq.mientradita.service.dto.TeamDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TeamService {
    @Autowired
    private lateinit var teamRepository: TeamRepository

    @Autowired
    private lateinit var stadiumRepository: StadiumRepository

    @Transactional
    fun registerTeam(createTeamRequest: CreateTeamRequest): TeamDTO {
        teamRepository.findByName(createTeamRequest.name).ifPresent { throw AlreadyExistsException("El equipo ya fue registrado") }

        val team = createTeamRequest.toModel()
        stadiumRepository.save(team.stadium)
        teamRepository.save(team)

        return TeamDTO.fromModel(team)
    }

    @Transactional
    fun getTeams(): List<TeamDTO> {
        return teamRepository.findAll().map { team -> TeamDTO.fromModel(team) }
    }

    @Transactional
    fun getTeamDetails(teamName: String): TeamDTO {
        val team = teamRepository.findByName(teamName).orElseThrow { throw TeamNotFoundException(teamName) }
        return TeamDTO.fromModel(team)
    }
}

