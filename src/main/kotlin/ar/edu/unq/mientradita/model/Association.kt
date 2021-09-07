package ar.edu.unq.mientradita.model

import ar.edu.unq.mientradita.model.exception.DuplicatedGameException

class Association {
    val games: MutableList<Game> = mutableListOf()

    fun createGame(local: Team, visitant: Team): Game {
        val newGame = Game(local, visitant)
        verifyGameIsNotCreated(newGame)

        games.add(newGame)

        return newGame
    }

    private fun verifyGameIsNotCreated(game: Game) {
        if(wasCreated(game)) throw DuplicatedGameException(game)
    }

    private fun wasCreated(game:Game) = games.any{actualGame -> actualGame.isEquals(game)}

}