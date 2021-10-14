package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.Team
import ar.edu.unq.mientradita.model.exception.TeamAlredyRegisteredException
import ar.edu.unq.mientradita.persistence.TeamRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.validation.constraints.NotBlank

@Service
class TeamService {
    @Autowired
    private lateinit var teamRepository: TeamRepository

    @Transactional
    fun registerTeam(createTeamRequest: CreateTeamRequest): TeamDTO {
        if(teamRepository.findByName(createTeamRequest.name).isPresent) { throw TeamAlredyRegisteredException() }
        val team = teamRepository.save(createTeamRequest.toModel())

        return TeamDTO.fromModel(team)
    }

    @Transactional
    fun getTeams(): List<TeamDTO> {
        return teamRepository.findAll().map { team -> TeamDTO(team.id!!, team.name) }
    }
}

class CreateTeamRequest(
        @field:NotBlank(message = "El nombre del equipo es requerido")
        val name: String,
        @field:NotBlank(message = "El nombre conocido del equipo es requerido")
        val knowName: String,
        @field:NotBlank(message = "El nombre del estadio del equipo es requerido")
        val stadium: String){
    fun toModel() = Team(this.name, this.knowName, this.stadium)
}

data class TeamDTO(val id: Long, val name: String){
    companion object {
        fun fromModel(team: Team): TeamDTO {
            return TeamDTO(team.id!!, team.name)
        }
    }
}