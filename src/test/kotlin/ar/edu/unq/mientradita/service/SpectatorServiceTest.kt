package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.builders.SpectatorBuilder
import ar.edu.unq.mientradita.webservice.controllers.CreateMatchRequest
import ar.edu.unq.mientradita.webservice.controllers.LoginRequest
import ar.edu.unq.mientradita.webservice.controllers.RegisterRequest
import ar.edu.unq.mientradita.model.exception.InvalidCredentialsException
import ar.edu.unq.mientradita.model.exception.TeamNotRegisteredException
import ar.edu.unq.mientradita.model.exception.UserAlreadyHasTicket
import ar.edu.unq.mientradita.model.exception.UsernameAlreadyRegistered

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

    @Autowired
    private lateinit var teamService: TeamService

    private val horarioPartido = LocalDateTime.of(2021, 10, 20, 16, 0)
    private val cargaDePartido = horarioPartido.minusMonths(1)
    private var nombreEquipoVisitante = "river"
    private var nombreEquipoLocal = "racing"
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
        teamService.registerTeam(CreateTeamRequest(nombreEquipoLocal, "un apodo", "un estadio"))
        teamService.registerTeam(CreateTeamRequest(nombreEquipoVisitante, "un apodo", "un estadio"))
        teamService.registerTeam(CreateTeamRequest("Boca", "un apodo", "un estadio"))
        teamService.registerTeam(CreateTeamRequest("Velez", "un apodo", "un estadio"))

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

        val exception = assertThrows<UsernameAlreadyRegistered> { authUserService.createSpectator(
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
        val exception = assertThrows<InvalidCredentialsException> { authUserService.login(LoginRequest("nico0510", "incorrecto")) }

        assertThat(exception.message).isEqualTo("Las credenciales introducidas son incorrectas, intente de nuevo")
    }

    @Test
    fun `un espectador puede reservar una entrada`() {
        val partidoDTO = matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreEquipoVisitante, 500.00, horarioPartido), cargaDePartido)
        val entradaReservada = spectatorService.reserveTicket(espectador.id, partidoDTO.id, horarioPartido.minusDays(4))

        assertThat(entradaReservada)
                .isEqualTo(TicketDTO(entradaReservada.id, espectador.id, partidoDTO.id, nombreEquipoLocal, nombreEquipoVisitante, horarioPartido))
    }

    @Test
    fun `un espectador no puede reservar mas de una entrada del mismo partido`() {
        val partidoDTO = matchService.createMatch(CreateMatchRequest("Boca", "Velez", 500.00, horarioPartido), cargaDePartido)
        spectatorService.reserveTicket(espectador.id, partidoDTO.id, horarioPartido.minusDays(4))

        val exception = assertThrows<UserAlreadyHasTicket> { spectatorService.reserveTicket(espectador.id, partidoDTO.id, horarioPartido.minusDays(2)) }

        assertThat(exception.message).isEqualTo("Ya tienes una entrada para este partido")
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
        val partido1 = matchService.createMatch(CreateMatchRequest("Boca", "Velez", 500.00, horarioPartido), cargaDePartido)
        val partido2 = matchService.createMatch(CreateMatchRequest(nombreEquipoVisitante, nombreEquipoLocal, 500.00, horarioPartido.plusDays(5)), cargaDePartido)

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
        val partido1 = matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreEquipoVisitante, 500.00, horarioPartido), cargaDePartido)
        val partido2 = matchService.createMatch(CreateMatchRequest(nombreEquipoVisitante, nombreEquipoLocal, 500.00, horarioPartido.plusDays(5)), cargaDePartido)

        val entrada1 = spectatorService.reserveTicket(espectadorDTO.id, partido1.id)
        val entrada2 = spectatorService.reserveTicket(espectadorDTO.id, partido2.id)

        matchService.comeIn(espectadorDTO.id, partido1.id, horarioPartido.minusHours(1))

        assertThat(spectatorService.pendingTickets(espectadorDTO.id, horarioPartido))
                .doesNotContain(entrada1)
                .contains(entrada2)
    }

    @Test
    fun `un espectador comienza sin tener un equipo favorito`() {
        assertThat(spectatorService.favouriteTeamFor(espectador.id)).isNull()
    }

    @Test
    fun `un espectador puede marcar como favorito un equipo`() {
        val team = teamService.getTeamDetails(nombreEquipoLocal)

        spectatorService.markAsFavourite(espectador.id, team.id)

        assertThat(spectatorService.favouriteTeamFor(espectador.id)).isEqualTo(team)
    }

    @Test
    fun `un espectador NO puede marcar como favorito un equipo inexistente`() {
        val exception = assertThrows<TeamNotRegisteredException> {
            spectatorService.markAsFavourite(espectador.id, 999999)
        }

        assertThat(exception.message).isEqualTo("Equipo no registrado")
    }

    @Test
    fun `un espectador puede cambiar de equipo favorito`() {
        val equipoA = teamService.getTeamDetails(nombreEquipoLocal)
        val equipoB = teamService.getTeamDetails(nombreEquipoVisitante)
        spectatorService.markAsFavourite(espectador.id, equipoA.id)

        spectatorService.markAsFavourite(espectador.id, equipoB.id)

        assertThat(spectatorService.favouriteTeamFor(espectador.id)).isEqualTo(equipoB)
    }

    @Test
    fun `se pueden obtener los fans de los equipos que van a jugar un partido`() {
        val otroEspectador = authUserService.createSpectator(RegisterRequest("", "", "juancito02", "1234", 42299502, "correosalvaje@gmail.com"))
        val equipoA = teamService.getTeamDetails(nombreEquipoLocal)
        val equipoB = teamService.getTeamDetails(nombreEquipoVisitante)
        spectatorService.markAsFavourite(espectador.id, equipoA.id)
        spectatorService.markAsFavourite(otroEspectador.id, equipoB.id)

        val partido = matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreEquipoVisitante, 500.00, horarioPartido), cargaDePartido)

        val obtainedFans = spectatorService.fansFrom(partido.id)
        assertThat(obtainedFans).hasSize(2)
        assertThat(obtainedFans[0].id).isEqualTo(espectador.id)
        assertThat(obtainedFans[1].id).isEqualTo(otroEspectador.id)
    }

    @Test
    fun `no se pueden obtener los fans de los equipos de un partido inexistente`() {
        val exception = assertThrows<MatchDoNotExistsException> {
            spectatorService.fansFrom(999999999)
        }

        assertThat(exception.message).isEqualTo("Partido no encontrado")
    }



    @AfterEach
    fun tearDown() {
        matchService.clearDataSet()
    }
}