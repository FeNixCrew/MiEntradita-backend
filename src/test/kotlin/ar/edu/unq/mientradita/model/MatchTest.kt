package ar.edu.unq.mientradita.model

import ar.edu.unq.mientradita.model.builders.SpectatorBuilder
import ar.edu.unq.mientradita.model.builders.MatchBuilder
import ar.edu.unq.mientradita.model.builders.TeamBuilder
import ar.edu.unq.mientradita.model.builders.TicketBuilder
import ar.edu.unq.mientradita.model.exception.DifferentGameException
import ar.edu.unq.mientradita.model.exception.InvalidClosingTimeException
import ar.edu.unq.mientradita.model.exception.InvalidOpeningTimeException
import ar.edu.unq.mientradita.model.exception.TicketsOffException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class MatchTest {
    lateinit var partido: Match
    lateinit var entrada: Ticket
    lateinit var horaDelPartido: LocalDateTime
    @BeforeEach
    fun setUp() {
        horaDelPartido = LocalDateTime.of(2021, 1, 1, 16, 30)
        partido = MatchBuilder().withMatchStart(horaDelPartido).build()
        entrada = TicketBuilder().build()
    }

    @Test
    fun `al reservar una entrada se decrementa en uno la cantidad de entradas disponibles`() {
        partido = MatchBuilder()
            .withAvailableTickets(10)
            .withMatchStart(horaDelPartido)
            .build()
        val hincha = SpectatorBuilder().build()
        val horaDeCompraDeEntrada = horaDelPartido.minusDays(5)
        val entradasAntesDeQueUnHinchaReserve = partido.availableTickets

        partido.reserveTicket(hincha, horaDeCompraDeEntrada)

        assertThat(partido.availableTickets).isEqualTo(entradasAntesDeQueUnHinchaReserve - 1)
    }

    @Test
    fun `no se puede reservar una entrada para un partido que ya no tiene entradas`(){
        partido = MatchBuilder()
                .withMatchStart(horaDelPartido)
                .build()
        val hincha = SpectatorBuilder().build()
        val horaDeCompraDeEntrada = horaDelPartido.minusDays(5)

        val excepcion = assertThrows<TicketsOffException> {
            partido.reserveTicket(hincha, horaDeCompraDeEntrada)
        }

        assertThat(excepcion.message).isEqualTo("Ya no hay mas entradas")
    }

    @Test
    fun `se puede entrar a un partido a partir de tres horas antes de que comience`() {
        partido = MatchBuilder()
            .withMatchStart(horaDelPartido)
            .build()
        entrada = TicketBuilder()
            .withGame(partido)
            .build()

        partido.comeIn(entrada, horaDelPartido.minusHours(3))

        assertThat(entrada.state).isEqualTo(Attend.PRESENT)
    }

    @Test
    fun `se puede entrar a un partido hasta noveta minutos luego de que comience`() {
        partido = MatchBuilder()
            .withAvailableTickets(10)
            .withMatchStart(horaDelPartido)
            .build()
        entrada = TicketBuilder()
            .withGame(partido)
            .build()

        partido.comeIn(entrada, horaDelPartido.plusMinutes(90))

        assertThat(entrada.state).isEqualTo(Attend.PRESENT)
    }

    @Test
    fun `no se puede entrar a un partido con una entrada que pertenece a otro juego`(){
        val local = TeamBuilder().withName("racing").build()
        partido = MatchBuilder().withHome(local).build()
        entrada = TicketBuilder().build()

        val exception = assertThrows<DifferentGameException> {
            partido.comeIn(entrada, LocalDateTime.now())
        }

        assertThat(exception.message).isEqualTo("La entrada pertenece a otro partido")
    }

    @Test
    fun `no se puede entrar a un partido en una fecha menor a tres horas antes del partido`(){
        partido = MatchBuilder()
            .withMatchStart(horaDelPartido)
            .build()
        entrada = TicketBuilder()
            .withGame(partido)
            .build()

        val horarioAnteriorAPoderEntrarAlPartido = horaDelPartido.minusHours(4)
        val exception = assertThrows<InvalidOpeningTimeException> {
            partido.comeIn(entrada, horarioAnteriorAPoderEntrarAlPartido)
        }

        assertThat(exception.message).isEqualTo("Aun no se puede ingresar al partido")
    }

    @Test
    fun `no se puede entrar a un partido en una fecha mayor a una hora y media luego del partido`(){
        partido = MatchBuilder()
            .withMatchStart(horaDelPartido)
            .build()
        entrada = TicketBuilder()
            .withGame(MatchBuilder().build())
            .build()

        val horarioAntesDePoderEntrarAlPartido = horaDelPartido.plusMinutes(91)
        val exception = assertThrows<InvalidClosingTimeException> {
            partido.comeIn(entrada, horarioAntesDePoderEntrarAlPartido)
        }

        assertThat(exception.message).isEqualTo("Ya no se puede ingresar al partido")
    }
}