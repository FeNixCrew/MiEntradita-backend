package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.Spectator

interface SpectatorService {

    fun createSpectator(name: String, surname: String, username: String, password: String, email: String, dni: Int): Spectator

    fun findSpectatorById(id: Long): Spectator


}