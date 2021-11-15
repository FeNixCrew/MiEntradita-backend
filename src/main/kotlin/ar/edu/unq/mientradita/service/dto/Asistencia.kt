package ar.edu.unq.mientradita.service.dto

data class AsistenciaDeEspectador(
    val id: Long,
    val dni: Int,
    val nombre: String,
    val asistencia: Asistencia,
)

enum class Asistencia { PRESENTE, AUSENTE, PENDIENTE; }