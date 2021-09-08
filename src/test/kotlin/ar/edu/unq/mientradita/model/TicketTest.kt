package ar.edu.unq.mientradita.model

import ar.edu.unq.mientradita.model.exception.OldTicketException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.time.Month

class TicketTest {
    lateinit var ticket: Ticket
    lateinit var game: Game
    lateinit var river: Team
    lateinit var defe: Team

    @BeforeEach
    fun setUp() {
        river = Team("River Plate")
        defe = Team("Defensa y Justicia")
        game = Game(river, defe)
        val date = LocalDateTime.of(2021, 9, 12, 16, 0, 0)
        ticket = Ticket(reservation = date, game = game)

    }

    // TODO revisar nombres de los tests

    @Test
    fun `a ticket has a date and time`() {
        assertThat(ticket.reservation.year).isEqualTo(2021)
        assertThat(ticket.reservation.month).isEqualTo(Month.SEPTEMBER)
        assertThat(ticket.reservation.dayOfMonth).isEqualTo(12)
        assertThat(ticket.reservation.hour).isEqualTo(16)
        assertThat(ticket.reservation.minute).isEqualTo(0)
    }

    @Test
    fun `a ticket is pending for use`() {
        assertThat(ticket.state).isEqualTo(StateOfTicket.PENDING)
    }

    @Test
    fun `a ticket was not initially used`() {
        assertThat(ticket.wasAttended()).isFalse
    }


    @Test
    fun `the ticket was presented dlkasdnmlas`() {
        ticket.makeAsPresent()
        assertThat(ticket.state).isEqualTo(StateOfTicket.PRESENT)
    }

    @Test
    fun `a ticket was used`() {
        ticket.makeAsPresent()
        assertThat(ticket.wasAttended()).isTrue
    }

    @Test
    fun `the ticket was not presented`() {
        ticket.makeAsAbsent()
        assertThat(ticket.state).isEqualTo(StateOfTicket.ABSENT)
    }

    @Test
    fun `a ticket is associated with a specific game`() {
        val partidoEsperado = Game(river, defe)

        assertThat(ticket.game).matches {
            it.local.name == partidoEsperado.local.name &&
                    it.visitant.name == partidoEsperado.visitant.name
        }

    }

    @Test
    fun `a user cannot use a ticket that has already expired `() {
        val ticket = Ticket(LocalDateTime.of(2021, 9, 6, 16, 0, 0), game = game)

        val exception = assertThrows<OldTicketException> {
            ticket.makeAsPresent()
        }

        assertThat(exception.message).isEqualTo("Esta entrada para el partido de River Plate vs Defensa y Justicia ya expiro")

    }

    @Test
    fun `a ticket that has already expired has already been attended `() {
        val ticket = Ticket(LocalDateTime.of(2021, 9, 6, 16, 0, 0), game = game)

        assertThrows<OldTicketException> {
            ticket.makeAsPresent()
        }

        assertThat(ticket.wasAttended()).isTrue
    }
}