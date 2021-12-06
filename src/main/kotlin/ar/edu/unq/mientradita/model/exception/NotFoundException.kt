package ar.edu.unq.mientradita.model.exception

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.user.Spectator

open class NotFoundException(message:String): MiEntraditaException(message)

class MatchNotFoundException : NotFoundException("Partido no encontrado")

class SpectatorNotRegistered : NotFoundException("El espectador no esta registrado")

class TeamNotFoundException(teamName: String): NotFoundException("Equipo llamado $teamName no encontrado")

class TeamNotRegisteredException: NotFoundException("Equipo no registrado")

class TicketFromMatchNotFoundException(spectator: Spectator, match: Match) :
    NotFoundException("No se ha encontrado una entrada reservada de ${spectator.fullname()} para el partido ${match.home} vs ${match.away}")
