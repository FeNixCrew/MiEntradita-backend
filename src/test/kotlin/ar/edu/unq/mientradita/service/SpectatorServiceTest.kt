package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.service.MatchService
import ar.edu.unq.mientradita.service.SpectatorDTO
import ar.edu.unq.mientradita.service.SpectatorService
import ar.edu.unq.mientradita.webservice.LoginRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.lang.RuntimeException
import java.time.LocalDateTime

@SpringBootTest
class SpectatorServiceTest {

    @Autowired
    private lateinit var spectatorService: SpectatorService

    @Autowired
    private lateinit var matchService: MatchService

    private val horarioPartido = LocalDateTime.of(2021, 10, 20, 16, 0)
    private var equipoVisitante = "river"
    private var equipoLocal = "racing"

    @Test
    fun `se puede crear un nuevo espectador`() {
        val espectador = spectatorService.createSpectator(
            name = "Nicolas",
            surname = "Martinez",
            username = "nico0510",
            password = "1234",
            email = "nico0510@gmail.com",
            dni = 12345678
        )
        val espectadorEsperado = spectatorService.findSpectatorById(espectador.id!!)

        assertThat(espectador)
            .usingRecursiveComparison()
            .ignoringFields("favoriteTeams", "tickets")
            .isEqualTo(espectadorEsperado)
    }

    @Test
    fun `se puede obtener informacion de un espectador al introducir correctamente sus credenciales`() {
        val espectador = spectatorService.createSpectator(
            name = "Nicolas",
            surname = "Martinez",
            username = "nico0510",
            password = "1234",
            email = "nico0510@gmail.com",
            dni = 12345678
        )
        val espectadorEncontrado = spectatorService.login(LoginRequest("nico0510", "1234"))

        assertThat(SpectatorDTO.fromModel(espectador)).usingRecursiveComparison().isEqualTo(espectadorEncontrado)
    }

    @Test
    fun `no se puede obtener informacion de un espectador si se introduce mal sus credenciales`() {
        val exception = assertThrows<RuntimeException> { spectatorService.login(LoginRequest("nico0510", "incorrecto")) }

        assertThat(exception.message).isEqualTo("Las credenciales introducidas son incorrectas, intente de nuevo.")
    }

    @Test
    fun `un espectador puede reservar un ticket`() {
        val espectadorAntesDeReservarTicket = spectatorService.createSpectator(
                name = "Nicolas",
                surname = "Martinez",
                username = "nico0510",
                password = "1234",
                email = "nico0510@gmail.com",
                dni = 12345678
        )

        val partido = matchService.createMatch(equipoLocal, equipoVisitante, 500.00, horarioPartido)

        val espectadorDespuesDeReservarTicket = spectatorService.reserveTicket(espectadorAntesDeReservarTicket.id!!, partido.id!!, horarioPartido.minusDays(4))

        assertThat(espectadorAntesDeReservarTicket.haveTickets()).isFalse
        assertThat(espectadorDespuesDeReservarTicket.haveTickets()).isTrue
    }

    @AfterEach
    fun tearDown() {
        matchService.clearDataSet()
    }
}