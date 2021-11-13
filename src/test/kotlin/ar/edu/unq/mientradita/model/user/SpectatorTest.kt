package ar.edu.unq.mientradita.model.user

import ar.edu.unq.mientradita.model.builders.SpectatorBuilder
import ar.edu.unq.mientradita.model.builders.TeamBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SpectatorTest {

    private lateinit var espectador: Spectator

    @BeforeEach
    fun setUp() {
        espectador = SpectatorBuilder().build()
    }

    @Test
    fun `un espectador comienza sin un equipo favorito`() {
        assertThat(espectador.favouriteTeam).isNull()
    }

    @Test
    fun `un espectador puede marcar a un equipo como favorito`() {
        val team = TeamBuilder().build()
        espectador.markAsFavourite(team)

        assertThat(espectador.favouriteTeam).isEqualTo(team)
    }


    @Test
    fun `un espectador puede cambiar de equipo favorito`() {
        val teamA = TeamBuilder().withName("Boca").build()
        val teamB = TeamBuilder().withName("River").build()
        espectador.markAsFavourite(teamA)

        espectador.markAsFavourite(teamB)

        assertThat(espectador.favouriteTeam).isEqualTo(teamB)
    }

    @Test
    fun `vuelvo a marcar el mismo equipo como favorito y ahora no tengo equipo favorito`() {
        val teamA = TeamBuilder().build()
        espectador.markAsFavourite(teamA)
        espectador.markAsFavourite(teamA)

        assertThat(espectador.favouriteTeam).isEqualTo(null)
    }
}