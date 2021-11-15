package ar.edu.unq.mientradita.service.dto

import javax.validation.constraints.NotBlank

data class LoginRequest(
        @field:NotBlank(message = "El usuario es requerido")
        val username: String,
        @field:NotBlank(message = "La contrasenia es requerida")
        val password: String)