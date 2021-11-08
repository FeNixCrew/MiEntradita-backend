package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.webservice.controllers.CreateMatchRequest
import ar.edu.unq.mientradita.webservice.controllers.RegisterRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class AdminTest {

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

        teamService.registerTeam(CreateTeamRequest(river, "un apodo", "un estadio", 20))
        teamService.registerTeam(CreateTeamRequest(racing, "un apodo", "un estadio", 20))
        teamService.registerTeam(CreateTeamRequest(velez, "un apodo", "un estadio", 20))
        partido = matchService.createMatch(CreateMatchRequest(river, racing, 500.00, horarioPartido), cargaDePartido)

    }


    @Test
    fun `un admin puede pedir la informacion de usuarios que fueron a un partido`() {
        spectatorService.reserveTicket(espectador.id, partido.id)

        assertThat(adminService.getMatchInformation(partido.id)).isNotEmpty

    }

    @Test
    fun `cuando un partido no tuvo ventas aun, no hay datos del partido`() {
        assertThat(adminService.getMatchInformation(partido.id)).isEmpty()
    }

    @Test
    fun `un espectador asiste a un partido y el admin sabe sus datos`() {
        spectatorService.reserveTicket(espectador.id, partido.id)
        matchService.comeIn(espectador.id, partido.id, horarioPartido)

        val espectadorReal = spectatorService.obtainSpectator(espectador.id)!!
        val espectadorInfo = adminService.getMatchInformation(partido.id).first()

        assertThat(espectadorInfo.spectatorId).isEqualTo(espectadorReal.id)
        assertThat(espectadorInfo.spectatorDni).isEqualTo(espectadorReal.dni)
        assertThat(espectadorInfo.spectatorName).isEqualTo(espectadorReal.name)
        assertThat(espectadorInfo.checkIn).isEqualTo(partido.matchStartTime)
        assertThat(espectadorInfo.isPresent).isTrue
    }

    @Test
    fun `un espectador compra una entrada pero no asiste al partido y el admin sabe sus datos`() {
        spectatorService.reserveTicket(espectador.id, partido.id)

        val espectadorReal = spectatorService.obtainSpectator(espectador.id)!!
        val espectadorInfo = adminService.getMatchInformation(partido.id).first()

        assertThat(espectadorInfo.spectatorId).isEqualTo(espectadorReal.id)
        assertThat(espectadorInfo.spectatorDni).isEqualTo(espectadorReal.dni)
        assertThat(espectadorInfo.spectatorName).isEqualTo(espectadorReal.name)
        assertThat(espectadorInfo.isPresent).isFalse
        assertThat(espectadorInfo.checkIn).isNull()
    }

    @AfterEach
    fun tearDown() {
        matchService.clearDataSet()
    }

}