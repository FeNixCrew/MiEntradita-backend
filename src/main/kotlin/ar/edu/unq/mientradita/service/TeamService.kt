package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.exception.TeamAlredyRegisteredException
import ar.edu.unq.mientradita.model.exception.TeamNotFoundException
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

    @Transactional
    fun registerTeam(createTeamRequest: CreateTeamRequest): TeamDTO {
        if (teamRepository.findByName(createTeamRequest.name).isPresent) {
            throw TeamAlredyRegisteredException()
        }
        val team = teamRepository.save(createTeamRequest.toModel())

        return TeamDTO.fromModel(team)
    }

    @Transactional
    fun getTeams(): List<TeamDTO> {
        return teamRepository.findAll().map { team -> TeamDTO(team.id!!, team.name, team.knowName, team.stadium, team.stadiumCapacity) }
    }

    @Transactional
    fun getTeamDetails(teamName: String): TeamDTO {
        val team = teamRepository.findByName(teamName).orElseThrow { throw TeamNotFoundException(teamName) }
        return TeamDTO.fromModel(team)
    }
}

