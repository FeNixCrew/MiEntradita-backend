package ar.edu.unq.mientradita.service.dto

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank

data class ComeInRequest(val spectatorId: Long, val matchId: Long)
data class CreateMatchRequest(
    @field:NotBlank(message = "El equipo local es requerido")
    val home: String,
    @field:NotBlank(message = "El equipo visitante es requerido")
    val away: String,
    val ticketPrice: Float,
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val matchStartTime: LocalDateTime,
    val admittedPercentage: Int? = 50
)