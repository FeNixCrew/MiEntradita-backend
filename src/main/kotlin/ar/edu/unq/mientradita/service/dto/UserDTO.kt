package ar.edu.unq.mientradita.service.dto

import ar.edu.unq.mientradita.model.user.MiEntraditaUser


data class UserDTO(val id: Long, val username: String, val role: String, val email: String) {
    companion object {
        fun fromModel(user: MiEntraditaUser): UserDTO {
            return UserDTO(user.id!!, user.username, user.role.toString(), user.email)
        }
    }
}