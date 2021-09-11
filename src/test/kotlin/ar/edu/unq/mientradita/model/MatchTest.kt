package ar.edu.unq.mientradita.model

import ar.edu.unq.mientradita.model.builders.MatchBuilder
import ar.edu.unq.mientradita.model.builders.TicketBuilder
import ar.edu.unq.mientradita.model.exception.DifferentGameException
import ar.edu.unq.mientradita.model.exception.InvalidClosingTimeException
import ar.edu.unq.mientradita.model.exception.InvalidOpeningTimeException
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
        partido = MatchBuilder().withHome("river").build()
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