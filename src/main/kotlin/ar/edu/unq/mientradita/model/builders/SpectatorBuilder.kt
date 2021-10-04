package ar.edu.unq.mientradita.model.builders

import ar.edu.unq.mientradita.model.user.Spectator

class SpectatorBuilder {
    private var name: String = ""
    private var surname: String = ""
    private var username: String = ""
    private var password: String = ""
    private var email: String = ""
    private var dni: String = "12345678"

    fun build(): Spectator {
        return Spectator(name, surname, username, email, dni, password)
    }

    fun withName(name: String): SpectatorBuilder {
        this.name = name
        return this
    }

    fun withSurname(surname: String): SpectatorBuilder {
        this.surname = surname
        return this
    }

    fun withUsername(username: String): SpectatorBuilder {
        this.username = username
        return this
    }

    fun withPassword(password: String): SpectatorBuilder {
        this.password = password
        return this
    }

    fun withEmail(email: String): SpectatorBuilder {
        this.email = email
        return this
    }

    fun withDni(dni: String): SpectatorBuilder {
        this.dni = dni
        return this
    }

}
