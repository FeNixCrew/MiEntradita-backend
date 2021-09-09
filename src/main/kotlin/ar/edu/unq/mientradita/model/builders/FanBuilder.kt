package ar.edu.unq.mientradita.model.builders

import ar.edu.unq.mientradita.model.Fan

class FanBuilder {
    private var name: String = ""
    private var surname: String = ""
    private var username: String = ""
    private var email: String = ""
    private var dni: Int = 12345678

    fun build(): Fan {
        return Fan(name, surname, username, email, dni)
    }

    fun withName(name: String): FanBuilder {
        this.name = name
        return this
    }

    fun withSurname(surname: String): FanBuilder {
        this.surname = surname
        return this
    }

    fun withUsername(username: String): FanBuilder {
        this.username = username
        return this
    }

    fun withEmail(email: String): FanBuilder {
        this.email = email
        return this
    }

    fun withDni(dni: Int): FanBuilder {
        this.dni = dni
        return this
    }

}
