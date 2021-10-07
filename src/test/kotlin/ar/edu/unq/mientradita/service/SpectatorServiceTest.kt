package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.builders.SpectatorBuilder
import ar.edu.unq.mientradita.webservice.CreateMatchRequest
import ar.edu.unq.mientradita.webservice.LoginRequest
import ar.edu.unq.mientradita.webservice.RegisterRequest
import ar.edu.unq.mientradita.webservice.config.security.JWTUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
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

    @Autowired
    private lateinit var authUserService: AuthUserService

    private val horarioPartido = LocalDateTime.of(2021, 10, 20, 16, 0)
    private val cargaDePartido = horarioPartido.minusMonths(1)
    private var equipoVisitante = "river"
    private var equipoLocal = "racing"
    private lateinit var espectador: UserDTO

    @BeforeEach
    fun setUp() {
        espectador = authUserService.createSpectator(
                RegisterRequest(
                        name = "Nicolas",
                        surname = "Martinez",
                        username = "nico0510",
                        password = "1234",
                        email = "nico0510@gmail.com",
                        dni = 42579865
                )
        )
    }

    @Test
    fun `se puede crear un nuevo espectador`() {
        val nuevoEspectador = authUserService.createSpectator(
                RegisterRequest(
                        name = "Fede",
                        surname = "Sandoval",
                        username = "fede1234",
                        password = "9999",
                        email = "fede1234@gmail.com",
                        dni = 45456784
                )
        )

        assertThat(nuevoEspectador.id).isNotNull
    }

    @Test
    fun `no se puede crear un usuario con un nombre de usuario que ya existe`() {
        authUserService.createSpectator(
                RegisterRequest("Fede", "Sandoval", "fede1234", "9999", 45456784, "fede1234@gmail.com"))

        val exception = assertThrows<RuntimeException> { authUserService.createSpectator(
                RegisterRequest("Fede", "Sandoval", "fede1234", "9999", 45456784, "fede1234@gmail.com"))
        }

        assertThat(exception.message).isEqualTo("Nombre de usuario ya registrado")

    }

    @Test
    fun `se puede obtener informacion de un espectador al introducir correctamente sus credenciales`() {
        val parTokenEspectador = authUserService.login(LoginRequest("nico0510", "1234"))

        assertThat(espectador).usingRecursiveComparison().isEqualTo(parTokenEspectador.second)
    }

    @Test
    fun `no se puede obtener informacion de un espectador si se introduce mal sus credenciales`() {
        val exception = assertThrows<RuntimeException> { authUserService.login(LoginRequest("nico0510", "incorrecto")) }

        assertThat(exception.message).isEqualTo("Las credenciales introducidas son incorrectas, intente de nuevo")
    }

    @Test
    fun `un espectador puede reservar una entrada`() {
        val partidoDTO = matchService.createMatch(CreateMatchRequest(equipoLocal, equipoVisitante, 500.00, horarioPartido, "Estadio 1"), cargaDePartido)
        val entradaReservada = spectatorService.reserveTicket(espectador.id, partidoDTO.id, horarioPartido.minusDays(4))

        assertThat(entradaReservada)
                .isEqualTo(TicketDTO(entradaReservada.id, espectador.id, partidoDTO.id, equipoLocal, equipoVisitante, horarioPartido))
    }

    @Test
    fun `un espectador comienza sin entradas pendientes`() {
        val espectador = SpectatorBuilder().build()
        val espectadorDTO = authUserService.createSpectator(
                RegisterRequest(
                        espectador.name, espectador.surname, espectador.username,
                        espectador.password, espectador.dni, espectador.email
                )
        )

        assertThat(spectatorService.pendingTickets(espectadorDTO.id, horarioPartido)).isEmpty()
    }

    @Test
    fun `se puede obtener la informacion de las entradas pendientes de un espectador`() {
        val espectador = SpectatorBuilder().build()
        val espectadorDTO = authUserService.createSpectator(
                RegisterRequest(
                        espectador.name, espectador.surname, espectador.username,
                        espectador.password, espectador.dni, espectador.email)
        )
        val partido1 = matchService.createMatch(CreateMatchRequest(equipoLocal, equipoVisitante, 500.00, horarioPartido, "Estadio 1"), cargaDePartido)
        val partido2 = matchService.createMatch(CreateMatchRequest(equipoVisitante, equipoLocal, 500.00, horarioPartido.plusDays(5), "Estadio 1"), cargaDePartido)

        val entrada1 = spectatorService.reserveTicket(espectadorDTO.id, partido1.id)
        val entrada2 = spectatorService.reserveTicket(espectadorDTO.id, partido2.id)

        assertThat(spectatorService.pendingTickets(espectadorDTO.id, horarioPartido)).containsExactly(entrada1, entrada2)
    }

    @Test
    fun `no aparecen en las entradas pendientes de un espectador una entrada de un partido al que ya asistio`() {
        val espectador = SpectatorBuilder().build()
        val espectadorDTO = authUserService.createSpectator(
                RegisterRequest(
                        espectador.name, espectador.surname, espectador.username,
                        espectador.password, espectador.dni, espectador.email)
        )
        val partido1 = matchService.createMatch(CreateMatchRequest(equipoLocal, equipoVisitante, 500.00, horarioPartido, "Estadio 1"), cargaDePartido)
        val partido2 = matchService.createMatch(CreateMatchRequest(equipoVisitante, equipoLocal, 500.00, horarioPartido.plusDays(5), "Estadio 1"), cargaDePartido)

        val entrada1 = spectatorService.reserveTicket(espectadorDTO.id, partido1.id)
        val entrada2 = spectatorService.reserveTicket(espectadorDTO.id, partido2.id)

        matchService.comeIn(espectadorDTO.id, partido1.id, horarioPartido.minusHours(1))

        assertThat(spectatorService.pendingTickets(espectadorDTO.id, horarioPartido))
                .doesNotContain(entrada1)
                .contains(entrada2)
    }

    @AfterEach
    fun tearDown() {
        matchService.clearDataSet()
    }
}