package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.builders.SpectatorBuilder
import ar.edu.unq.mientradita.webservice.LoginRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
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

        assertThat(espectador.id).isNotNull
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

        assertThat(exception.message).isEqualTo("Las credenciales introducidas son incorrectas, intente de nuevo")
    }

    @Test
    fun `un espectador puede reservar una entrada`() {
        val espectador = spectatorService.createSpectator(
                name = "Nicolas",
                surname = "Martinez",
                username = "nico0510",
                password = "1234",
                email = "nico0510@gmail.com",
                dni = 12345678
        )

        val partido = matchService.createMatch(equipoLocal, equipoVisitante, 500.00, horarioPartido)

        val entradaReservada = spectatorService.reserveTicket(espectador.id!!, partido.id!!, horarioPartido.minusDays(4))

        assertThat(entradaReservada)
            .isEqualTo(TicketDTO(entradaReservada.id, espectador.id!!, partido.id!!, equipoLocal, equipoVisitante, horarioPartido))
    }

    @Test
    fun `un espectador comienza sin entradas pendientes`() {
        var espectador = SpectatorBuilder().build()
        espectador = spectatorService.createSpectator(
            espectador.name, espectador.surname, espectador.username,
            espectador.password, espectador.email, espectador.dni
        )

        assertThat(spectatorService.pendingTickets(espectador.id!!, horarioPartido)).isEmpty()
    }

    @Test
    fun `se puede obtener la informacion de las entradas pendientes de un espectador`() {
        var espectador = SpectatorBuilder().build()
        espectador = spectatorService.createSpectator(
            espectador.name, espectador.surname, espectador.username,
            espectador.password, espectador.email, espectador.dni
        )
        val partido1 = matchService.createMatch(equipoLocal, equipoVisitante, 500.00, horarioPartido)
        val partido2 = matchService.createMatch(equipoVisitante, equipoLocal, 500.00, horarioPartido)

        val entrada1 = spectatorService.reserveTicket(espectador.id!!, partido1.id!!)
        val entrada2 = spectatorService.reserveTicket(espectador.id!!, partido2.id!!)

        assertThat(spectatorService.pendingTickets(espectador.id!!, horarioPartido)).containsExactly(entrada1, entrada2)
    }

    @Test
    fun `no aparecen en las entradas pendientes de un espectador una entrada de un partido al que ya asistio`() {
        var espectador = SpectatorBuilder().build()
        espectador = spectatorService.createSpectator(
            espectador.name, espectador.surname, espectador.username,
            espectador.password, espectador.email, espectador.dni
        )
        val partido1 = matchService.createMatch(equipoLocal, equipoVisitante, 500.00, horarioPartido)
        val partido2 = matchService.createMatch(equipoVisitante, equipoLocal, 500.00, horarioPartido)

        val entrada1 = spectatorService.reserveTicket(espectador.id!!, partido1.id!!)
        val entrada2 = spectatorService.reserveTicket(espectador.id!!, partido2.id!!)

        matchService.comeIn(espectador.id!!,partido1.id!!,horarioPartido.minusHours(1))

        assertThat(spectatorService.pendingTickets(espectador.id!!, horarioPartido))
            .doesNotContain(entrada1)
            .contains(entrada2)
    }

    @AfterEach
    fun tearDown() {
        matchService.clearDataSet()
    }
}