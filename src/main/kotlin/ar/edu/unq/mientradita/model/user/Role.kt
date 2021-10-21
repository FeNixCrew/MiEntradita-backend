package ar.edu.unq.mientradita.model.user

import ar.edu.unq.mientradita.model.exception.MiEntraditaException

enum class Role {
    ROLE_ADMIN, ROLE_SCANNER, ROLE_USER;

    companion object {
        fun fromString(roleString: String): Role {
            val role = roleString.toUpperCase()
            return when (roleString.toUpperCase()) {
                ROLE_USER.toString() -> ROLE_USER
                ROLE_ADMIN.toString() -> ROLE_ADMIN
                ROLE_SCANNER.toString() -> ROLE_SCANNER
                else -> throw InvalidRoleException(role)
            }
        }
    }
}

class InvalidRoleException(invalidRole: String): MiEntraditaException("El rol $invalidRole no es valido")