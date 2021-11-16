package ar.edu.unq.mientradita.model

import ar.edu.unq.mientradita.model.builders.*
import ar.edu.unq.mientradita.model.exception.DifferentGameException
import ar.edu.unq.mientradita.model.exception.InvalidClosingTimeException
import ar.edu.unq.mientradita.model.exception.InvalidOpeningTimeException
import ar.edu.unq.mientradita.model.exception.InvalidPercentageException
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

        assertThat(entrada.wasPresent()).isTrue
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

        assertThat(entrada.wasPresent()).isTrue
    }

    @Test
    fun `no se puede entrar a un partido con una entrada que pertenece a otro juego`() {
        partido = MatchBuilder().withHome(TeamBuilder().withName("river").build()).build()
        entrada = TicketBuilder().build()

        val exception = assertThrows<DifferentGameException> {
            partido.comeIn(entrada, LocalDateTime.now())
        }

        assertThat(exception.message).isEqualTo("La entrada pertenece a otro partido")
    }

    @Test
    fun `no se puede entrar a un partido en una fecha menor a tres horas antes del partido`() {
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
    fun `no se puede entrar a un partido en una fecha mayor a una hora y media luego del partido`() {
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

    @Test
    fun `un partido sabe en que estadio se va a jugar`() {
        val estadio = StadiumBuilder().withName("Santiago Bernabeu").build()
        val team = TeamBuilder().withStadium(estadio).build()
        val partido = MatchBuilder().withHome(team).build()

        assertThat(partido.stadium()).isEqualTo(team.stadium)
    }

    @Test
    fun `un partido sabe la capacidad maxima de hinchas que puede albergar`() {
        val estadio = StadiumBuilder().withCapacity(500).build()
        val equipo = TeamBuilder().withStadium(estadio).build()
        val partido = MatchBuilder().withHome(equipo).build()

        assertThat(partido.maximumCapacity()).isEqualTo(equipo.stadium.capacity)
    }

    @Test
    fun `a un partido se le puede indicar cuanta capacidad de la total puede admitir`() {
        val estadio = StadiumBuilder().withCapacity(500).build()
        val equipo = TeamBuilder().withStadium(estadio).build()
        val partido = MatchBuilder().withHome(equipo).build()
        val capacidadEsperada = equipo.stadium.capacity * 50 / 100

        partido.admittedPercentage = 50

        assertThat(partido.maximumCapacity()).isEqualTo(capacidadEsperada)
    }

    @Test
    fun `un partido que no vendio entradas tiene todas las entradas para vender`() {
        val estadio = StadiumBuilder().withCapacity(500).build()
        val equipo = TeamBuilder().withStadium(estadio).build()
        val partido = MatchBuilder().withHome(equipo).build()

        assertThat(partido.numberOfTicketsAvailable()).isEqualTo(partido.maximumCapacity())
    }

    @Test
    fun `un partido vende una entrada y ya no tiene entradas para vender`() {
        val estadio = StadiumBuilder().withCapacity(1).build()
        val equipo = TeamBuilder().withStadium(estadio).build()
        val partido = MatchBuilder().withHome(equipo).build()
        val espectador = SpectatorBuilder().build()
        val cantidadDeTicketsParaVenderAntes = partido.numberOfTicketsAvailable()

        espectador.reserveATicketFor(partido)

        assertThat(cantidadDeTicketsParaVenderAntes).isGreaterThan(partido.numberOfTicketsAvailable())
        assertThat(partido.numberOfTicketsAvailable()).isEqualTo(0)
    }

    @Test
    fun `un partido no puede tener un porcentaje de aforo menor a 0 porciento`() {
        val estadio = StadiumBuilder().withCapacity(500).build()
        val equipo = TeamBuilder().withStadium(estadio).build()
        val partido = MatchBuilder().withHome(equipo).build()

        val exception = assertThrows<InvalidPercentageException> {
            partido.admittedPercentage = -1
        }

        assertThat(exception.message).isEqualTo("El porcentaje debe estar entre 0 y 100")
    }

    @Test
    fun `un partido no puede superar el 100 porciento de aforo`() {
        val estadio = StadiumBuilder().withCapacity(500).build()
        val equipo = TeamBuilder().withStadium(estadio).build()
        val partido = MatchBuilder().withHome(equipo).build()

        val exception = assertThrows<InvalidPercentageException> {
            partido.admittedPercentage = 101
        }

        assertThat(exception.message).isEqualTo("El porcentaje debe estar entre 0 y 100")
    }

    @Test
    fun `un partido puede tener un porcentaje de aforo hasta el 100 porciento`() {
        val estadio = StadiumBuilder().withCapacity(500).build()
        val equipo = TeamBuilder().withStadium(estadio).build()
        val partido = MatchBuilder().withHome(equipo).build()

        assertThat(partido.admittedPercentage).isEqualTo(100)
    }

}