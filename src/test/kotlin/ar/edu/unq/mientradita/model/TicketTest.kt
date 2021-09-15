package ar.edu.unq.mientradita.model

import ar.edu.unq.mientradita.model.builders.MatchBuilder
import ar.edu.unq.mientradita.model.builders.TicketBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.Month

class TicketTest {
    lateinit var entrada: Ticket
    lateinit var partido: Match
    lateinit var horarioDePartido: LocalDateTime

    @BeforeEach
    fun setUp() {
        horarioDePartido = LocalDateTime.of(2021, 9, 12, 16, 0, 0)
        partido = MatchBuilder().withMatchStart(horarioDePartido).build()
        entrada = TicketBuilder().withGame(partido).build()
    }

    @Test
    fun `una entrada sabe el momento en el que fue reservada`() {
        entrada = TicketBuilder()
                    .withReservation(horarioDePartido.minusDays(3))
                    .build()

        assertThat(entrada.reservation.year).isEqualTo(2021)
        assertThat(entrada.reservation.month).isEqualTo(Month.SEPTEMBER)
        assertThat(entrada.reservation.dayOfMonth).isEqualTo(horarioDePartido.dayOfMonth.minus(3))
        assertThat(entrada.reservation.hour).isEqualTo(16)
        assertThat(entrada.reservation.minute).isEqualTo(0)
    }

    @Test
    fun `una entrada esta relacionada con un partido`() {
        assertThat(entrada.match).isEqualTo(partido)
    }


    @Test
    fun `una entrada inicialmente no esta entregada`() {
        assertThat(entrada.wasPresent()).isFalse
    }

    @Test
    fun `cuando una entrada se marca como presente se registra la hora en la que fue marcada`() {
        val horaDeEntrada = horarioDePartido.minusHours(1)

        entrada.markAsPresent(horaDeEntrada)

        assertThat(entrada.presentTime).isEqualTo(horaDeEntrada)
        assertThat(entrada.wasPresent()).isTrue
    }

    @Test
    fun `una entrada esta ausente cuando el partido ya paso y no se asistio al mismo`() {
        assertThat(entrada.wasPresent()).isFalse
    }
}