package ar.edu.unq.mientradita.service.dto

import ar.edu.unq.mientradita.model.Team
import javax.validation.constraints.NotBlank

class CreateTeamRequest(
        @field:NotBlank(message = "El nombre del equipo es requerido")
        val name: String,
        @field:NotBlank(message = "El nombre conocido del equipo es requerido")
        val knowName: String,
        @field:NotBlank(message = "El nombre del estadio del equipo es requerido")
        val stadium: String,
        val stadiumCapacity: Int
) {
    fun toModel() = Team(this.name, this.knowName, this.stadium, this.stadiumCapacity)
}