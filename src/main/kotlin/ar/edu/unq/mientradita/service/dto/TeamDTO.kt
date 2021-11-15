package ar.edu.unq.mientradita.service.dto

import ar.edu.unq.mientradita.model.Team

data class TeamDTO(
        val id: Long,
        val name: String,
        val knowName: String,
        val stadium: String,
        val stadiumCapacity: Int) {
    companion object {
        fun fromModel(team: Team): TeamDTO {
            return TeamDTO(team.id!!, team.name, team.knowName, team.stadium, team.stadiumCapacity)
        }
    }
}