package ar.edu.unq.mientradita.model.exception

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.Spectator

class TicketFromMatchNotFoundException(spectator: Spectator, match: Match) :
    RuntimeException("No se ha encontrado una entrada reservada de ${spectator.fullname()} para el partido ${match.home.name} vs ${match.away.name}")
