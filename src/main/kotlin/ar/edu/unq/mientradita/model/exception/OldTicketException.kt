package ar.edu.unq.mientradita.model.exception

import ar.edu.unq.mientradita.model.Game
import java.lang.RuntimeException

class OldTicketException(game: Game) :
        RuntimeException("Esta entrada para el partido de ${game.local.name} vs ${game.visitant.name} ya expiro")