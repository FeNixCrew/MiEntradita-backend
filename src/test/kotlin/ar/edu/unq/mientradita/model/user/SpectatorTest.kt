package ar.edu.unq.mientradita.model.user

import ar.edu.unq.mientradita.model.builders.*
import ar.edu.unq.mientradita.model.exception.TicketNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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

    @Test
    fun `un espectador conoce las entradas pendientes de pago`() {
        val team = TeamBuilder().withStadium(StadiumBuilder().withCapacity(10).build()).build()
        val partido = MatchBuilder().withHome(team).build()
        val entradaReservada = espectador.reserveATicketFor(partido)

        assertThat(espectador.pendingPaymentTickets()).containsExactly(entradaReservada)
    }

    @Test
    fun `un espectador guarda el pago de una entrada`() {
        val team = TeamBuilder().withStadium(StadiumBuilder().withCapacity(10).build()).build()
        val partido = MatchBuilder().withHome(team).build()
        val entradaReservada = espectador.reserveATicketFor(partido)

        espectador.savePayedTicket(entradaReservada, "un id de pago")

        assertThat(espectador.pendingPaymentTickets()).isEmpty()
    }

    @Test
    fun `un espectador no puede guardar el pago de una entrada que no le pertenece`() {
        val otroEspectador = SpectatorBuilder().build()
        val team = TeamBuilder().withStadium(StadiumBuilder().withCapacity(10).build()).build()
        val partido = MatchBuilder().withHome(team).build()
        val entradaReservada = otroEspectador.reserveATicketFor(partido)

        val exception = assertThrows<TicketNotFoundException> {
            espectador.savePayedTicket(entradaReservada, "un id de pago")
        }
        assertThat(exception.message).isEqualTo("la entrada no pertenece al espectador")
    }
}
