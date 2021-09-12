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
    fun `una entrada inicialmente tiene un estado de pendiente`() {
        assertThat(entrada.state).isEqualTo(Attend.PENDING)
    }

    @Test
    fun `una entrada se puede marcar como presente`() {
        entrada.markAsPresent()
        assertThat(entrada.state).isEqualTo(Attend.PRESENT)
    }

    @Test
    fun `una entrada se puede marcar como ausente`() {
        entrada.markAsAbsent()
        assertThat(entrada.state).isEqualTo(Attend.ABSENT)
    }
}