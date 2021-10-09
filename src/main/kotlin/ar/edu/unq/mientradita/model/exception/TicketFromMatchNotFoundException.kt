package ar.edu.unq.mientradita.model.exception

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.user.Spectator

class TicketFromMatchNotFoundException(spectator: Spectator, match: Match) :
    MiEntraditaException("No se ha encontrado una entrada reservada de ${spectator.fullname()} para el partido ${match.home} vs ${match.away}")
