package ar.edu.unq.mientradita.service.impl

import ar.edu.unq.mientradita.model.exception.MatchDoNotExistsException
import ar.edu.unq.mientradita.model.exception.SpectatorNotRegistered
import ar.edu.unq.mientradita.service.MatchService
import ar.edu.unq.mientradita.service.SpectatorService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class MatchServiceTest {
    @Autowired
    private lateinit var matchService: MatchService

    @Autowired
    private lateinit var spectatorService: SpectatorService

    private val equipoLocal = "river"
    private val equipoVisitante = "racing"
    private val horarioPartido = LocalDateTime.of(2021, 9, 20, 16, 0)

    @Test
    fun `se pueden guardar partidos`() {
        val partido = matchService.createMatch(equipoLocal, equipoVisitante, 500.00, horarioPartido)

        assertThat(partido).usingRecursiveComparison().isEqualTo(matchService.findMatchBy(partido.id!!))
    }

    @Test
    fun `un espectador asiste a un partido`() {
        val espectador = spectatorService.createSpectator(
                name = "Nicolas",
                surname = "Martinez",
                username = "nico0510",
                password = "1234",
                email = "nico0510@gmail.com",
                dni = 12345678
        )
        val partido = matchService.createMatch(equipoLocal, equipoVisitante, 500.00, horarioPartido)
        spectatorService.reserveTicket(espectador.id!!, partido.id!!, horarioPartido.minusDays(4))

        matchService.comeIn(espectador.id!!, partido.id!!, horarioPartido)

        val ticketDespuesDeAsistirAlPartido = spectatorService.findTicketFrom(espectador.id!!, partido.id!!)
        assertThat(ticketDespuesDeAsistirAlPartido.isAttend()).isTrue
    }

    @Test
    fun `al asistir a un partido se ve un mensaje de bienvenida`() {
        val espectador = spectatorService.createSpectator(
                name = "Nicolas",
                surname = "Martinez",
                username = "nico0510",
                password = "1234",
                email = "nico0510@gmail.com",
                dni = 12345678
        )
        val partido = matchService.createMatch(equipoLocal, equipoVisitante, 500.00, horarioPartido)
        spectatorService.reserveTicket(espectador.id!!, partido.id!!, horarioPartido.minusDays(4))

        assertThat(matchService.comeIn(espectador.id!!, partido.id!!, horarioPartido))
                .isEqualTo("Bienvenido ${espectador.username} al partido de ${partido.home} vs ${partido.away}")
    }

    @Test
    fun `un espectador quiere reservar una entrada para un partido que no existe y no puede`() {
        val espectador = spectatorService.createSpectator(
                name = "Nicolas",
                surname = "Martinez",
                username = "nico0510",
                password = "1234",
                email = "nico0510@gmail.com",
                dni = 12345678
        )
        val idDePartidoInexistente = 9999.toLong()

        val excepcion = assertThrows<MatchDoNotExistsException> {
            spectatorService.reserveTicket(espectador.id!!, idDePartidoInexistente, horarioPartido.minusDays(2))
        }

        assertThat(excepcion.message).isEqualTo("Partido no encontrado")
    }

    @Test
    fun `un espectador que no esta registrado intenta reservar un ticket para un partido y es rechazado`() {
        val espectadorInexistenteId = 9999.toLong()
        val partido = matchService.createMatch(equipoLocal, equipoVisitante, 500.00, horarioPartido)


        val exception = assertThrows<SpectatorNotRegistered> {
            spectatorService.reserveTicket(espectadorInexistenteId, partido.id!!, horarioPartido.minusDays(2))
        }

        assertThat(exception.message).isEqualTo("El espectador no esta registrado")

    }

    @Test
    fun `un espectador intenta asistir a un partido y explota todo asasasasadsdas`() {
        val espectadorInexistenteId = 9999.toLong()
        val partido = matchService.createMatch(equipoLocal, equipoVisitante, 500.00, horarioPartido)

        val exception = assertThrows<SpectatorNotRegistered> {
            matchService.comeIn(espectadorInexistenteId, partido.id!!, horarioPartido)
        }

        assertThat(exception.message).isEqualTo("El espectador no esta registrado")
    }

    @Test
    fun `un espectador intenta asistir a un partido que no existe y explota todo asasasasadsdas`() {
        val espectador = spectatorService.createSpectator(
                name = "Nicolas",
                surname = "Martinez",
                username = "nico0510",
                password = "1234",
                email = "nico0510@gmail.com",
                dni = 12345678
        )
        val partidoInexistenteId = 9999.toLong()

        val exception = assertThrows<MatchDoNotExistsException> {
            matchService.comeIn(espectador.id!!, partidoInexistenteId, horarioPartido)
        }

        assertThat(exception.message).isEqualTo("Partido no encontrado")


    }
}