package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.service.dto.Asistencia
import ar.edu.unq.mientradita.service.dto.CreateTeamRequest
import ar.edu.unq.mientradita.service.dto.MatchDTO
import ar.edu.unq.mientradita.service.dto.UserDTO
import ar.edu.unq.mientradita.service.dto.CreateMatchRequest
import ar.edu.unq.mientradita.webservice.controllers.RegisterRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class AdminServiceTest {

    @Autowired
    private lateinit var matchService: MatchService

    @Autowired
    private lateinit var spectatorService: SpectatorService

    @Autowired
    private lateinit var authUserService: AuthUserService

    @Autowired
    private lateinit var teamService: TeamService

    @Autowired
    private lateinit var adminService: AdminService

    private val river = "river"
    private val racing = "racing"
    private val velez = "velez"
    private val horarioPartido = LocalDateTime.of(2021, 9, 20, 16, 15)
    private val cargaDePartido = horarioPartido.minusMonths(1)
    private lateinit var espectador: UserDTO
    private lateinit var partido: MatchDTO

    @BeforeEach
    fun setUp() {
        espectador = authUserService.createSpectator(
                RegisterRequest(
                        name = "Nicolas",
                        surname = "Martinez",
                        username = "nico0510",
                        password = "1234",
                        email = "nico0510@gmail.com",
                        dni = 12345678
                )
        )

        teamService.registerTeam(CreateTeamRequest(river, "un apodo", "un estadio", 20, 0.0, 0.0))
        teamService.registerTeam(CreateTeamRequest(racing, "un apodo", "un estadio", 20, 0.0, 0.0))
        teamService.registerTeam(CreateTeamRequest(velez, "un apodo", "un estadio", 20, 0.0, 0.0))
        partido = matchService.createMatch(CreateMatchRequest(river, racing, 500F, horarioPartido), cargaDePartido)

    }


    @Test
    fun `cuando un partido no tuvo reservas aun, no hay datos del partido`() {
        assertThat(adminService.attendanceFor(partido.id)).isEmpty()
    }

    @Test
    fun `se pueden obtener los espectadores que estan pendientes de asistencia a un partido antes de que termine`() {
        spectatorService.reserveTicket(espectador.id, partido.id)

        val asistenciaAlPartido = adminService.attendanceFor(partido.id, horarioPartido)

        assertThat(asistenciaAlPartido.all { it.asistencia == Asistencia.PENDIENTE }).isTrue
    }

    @Test
    fun `se pueden obtener los espectadores que estuvieron presentes en un partido`() {
        val otroEspectador = authUserService.createSpectator(
            RegisterRequest(
                name = "Federico",
                surname = "Sandoval",
                username = "chester",
                password = "1234",
                email = "chester@gmail.com",
                dni = 12345679
            )
        )
        spectatorService.reserveTicket(espectador.id, partido.id)
        spectatorService.reserveTicket(otroEspectador.id, partido.id)
        matchService.comeIn(espectador.id, partido.id, horarioPartido)
        matchService.comeIn(otroEspectador.id, partido.id, horarioPartido)

        val asistenciaAlPartido = adminService.attendanceFor(partido.id)

        assertThat(asistenciaAlPartido.all { it.asistencia == Asistencia.PRESENTE }).isTrue
    }

    @Test
    fun `se pueden obtener los espectadores que faltaron a un partido luego de que el partido haya terminado`() {
        spectatorService.reserveTicket(espectador.id, partido.id)

        val asistenciaAlPartido = adminService.attendanceFor(partido.id, horarioPartido.plusDays(3))

        assertThat(asistenciaAlPartido.all { it.asistencia == Asistencia.AUSENTE }).isTrue
    }

    @Test
    fun `se obtiene el estado de asistencias ordenando a las personas de mayor a menor en cuanto a edad`() {
        val espectadorJoven = authUserService.createSpectator(
            RegisterRequest(
                name = "Federico",
                surname = "Sandoval",
                username = "chester",
                password = "1234",
                email = "chester@gmail.com",
                dni = 22345678
            )
        )
        spectatorService.reserveTicket(espectadorJoven.id, partido.id)
        spectatorService.reserveTicket(espectador.id, partido.id)

        val asistenciaAlPartido = adminService.attendanceFor(partido.id)

        assertThat(asistenciaAlPartido.first().id).isEqualTo(espectador.id)
        assertThat(asistenciaAlPartido.last().id).isEqualTo(espectadorJoven.id)
    }

    @AfterEach
    fun tearDown() {
        matchService.clearDataSet()
    }

}