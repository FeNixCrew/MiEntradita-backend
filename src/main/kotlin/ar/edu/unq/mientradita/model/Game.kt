package ar.edu.unq.mientradita.model

class Game(val local: Team, val visitant: Team) {
    fun isEquals(game: Game) = this.local.isEquals(game.local) && this.visitant.isEquals(game.visitant)

}
