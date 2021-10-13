package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.Team
import ar.edu.unq.mientradita.persistence.TeamRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TeamService {
    @Autowired
    private lateinit var teamRepository: TeamRepository

    @Transactional
    fun registerTeam(
        createTeamRequest: CreateTeamRequest
    ): Team {

        return teamRepository.save(createTeamRequest.toModel())
    }
}

class CreateTeamRequest(val name: String, val knowName: String, val stadium: String){
    fun toModel() = Team(this.name, this.knowName, this.stadium)
}