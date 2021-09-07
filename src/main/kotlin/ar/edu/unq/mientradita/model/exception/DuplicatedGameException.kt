package ar.edu.unq.mientradita.model.exception

import ar.edu.unq.mientradita.model.Game

class DuplicatedGameException(game: Game):
    RuntimeException("El partido ya fue creado con ${game.local.name} de local y ${game.visitant.name} de visitante")
