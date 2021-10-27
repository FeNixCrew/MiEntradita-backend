package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.exception.*
import ar.edu.unq.mientradita.webservice.controllers.CreateMatchRequest
import ar.edu.unq.mientradita.webservice.controllers.RegisterRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
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

    @Autowired
    private lateinit var authUserService: AuthUserService

    @Autowired
    private lateinit var teamService: TeamService

    private val nombreEquipoLocal = "river"
    private val nombreEquipoVisitante = "racing"
    private val nombreOtroEquipo = "velez"
    private val horarioPartido = LocalDateTime.of(2021, 9, 20, 16, 15)
    private val cargaDePartido = horarioPartido.minusMonths(1)
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
                        dni = 12345678
                )
        )

        teamService.registerTeam(CreateTeamRequest(nombreEquipoLocal, "un apodo", "un estadio"))
        teamService.registerTeam(CreateTeamRequest(nombreEquipoVisitante, "un apodo", "un estadio"))
        teamService.registerTeam(CreateTeamRequest(nombreOtroEquipo, "un apodo", "un estadio"))
    }

    @Test
    fun `se pueden crear partidos`() {
        val partidoDTO = matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreEquipoVisitante, 500.00, horarioPartido), cargaDePartido)

        assertThat(partidoDTO).isNotNull
    }

    @Test
    fun `al asistir a un partido se ve un mensaje de bienvenida`() {
        val partidoDTO = matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreEquipoVisitante, 500.00, horarioPartido), cargaDePartido)
        spectatorService.reserveTicket(espectador.id, partidoDTO.id, horarioPartido.minusDays(4))

        assertThat(matchService.comeIn(espectador.id, partidoDTO.id, horarioPartido))
                .isEqualTo("Bienvenido ${espectador.username} al partido de ${partidoDTO.home} vs ${partidoDTO.away}")
    }

    @Test
    fun `un espectador quiere reservar una entrada para un partido que no existe y no puede`() {
        val idDePartidoInexistente = 9999.toLong()

        val excepcion = assertThrows<MatchDoNotExistsException> {
            spectatorService.reserveTicket(espectador.id, idDePartidoInexistente, horarioPartido.minusDays(2))
        }

        assertThat(excepcion.message).isEqualTo("Partido no encontrado")
    }

    @Test
    fun `un espectador que no esta registrado intenta reservar un ticket para un partido y es rechazado`() {
        val espectadorInexistenteId = 9999.toLong()
        val partidoDTO = matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreEquipoVisitante, 500.00, horarioPartido), cargaDePartido)


        val exception = assertThrows<SpectatorNotRegistered> {
            spectatorService.reserveTicket(espectadorInexistenteId, partidoDTO.id, horarioPartido.minusDays(2))
        }

        assertThat(exception.message).isEqualTo("El espectador no esta registrado")

    }

    @Test
    fun `un espectador no puede asistir a un partido que no existe`() {
        val partidoInexistenteId = 9999.toLong()

        val exception = assertThrows<MatchDoNotExistsException> {
            matchService.comeIn(espectador.id, partidoInexistenteId, horarioPartido)
        }

        assertThat(exception.message).isEqualTo("Partido no encontrado")
    }

    @Test
    fun `un espectador no puede asistir dos veces a un partido`() {
        val partidoDTO = matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreEquipoVisitante, 500.00, horarioPartido), cargaDePartido)
        spectatorService.reserveTicket(espectador.id, partidoDTO.id, horarioPartido.minusDays(4))
        matchService.comeIn(espectador.id, partidoDTO.id, horarioPartido)

        val exception = assertThrows<AlreadyPresentInGameException> { matchService.comeIn(espectador.id, partidoDTO.id, horarioPartido) }

        assertThat(exception.message).isEqualTo("El espectador ya ha ingresado al partido")
    }

    @Test
    fun `se pueden buscar partidos proximos por matcheo de nombre de un equipo`() {
        val partidosCreados = mutableListOf<MatchDTO>()
        partidosCreados.add(matchService.createMatch(CreateMatchRequest(nombreOtroEquipo, nombreEquipoVisitante, 500.00, horarioPartido), cargaDePartido))
        partidosCreados.add(matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreOtroEquipo, 500.00, horarioPartido.plusDays(7)), cargaDePartido))

        val partidos = matchService.searchNextMatchsByPartialName("vel", null, horarioPartido)

        assertThat(partidos).containsExactly(partidosCreados[0], partidosCreados[1])
    }

    @Test
    fun `se pueden buscar partidos luego de una fecha determinada`() {
        val partidosCreados = mutableListOf<MatchDTO>()
        partidosCreados.add(matchService.createMatch(CreateMatchRequest(nombreOtroEquipo, nombreEquipoVisitante, 500.00, horarioPartido), cargaDePartido))
        partidosCreados.add(matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreOtroEquipo, 500.00, horarioPartido.plusDays(7)), cargaDePartido))

        val partidos = matchService.searchNextMatchsByPartialName("", null, horarioPartido.plusDays(1))

        assertThat(partidos).doesNotContain(partidosCreados[0]).containsExactly(partidosCreados[1])
    }

    @Test
    fun `al buscar partidos, se encuentran ordenados por la fecha mas proxima`() {
        val partido1 = matchService.createMatch(CreateMatchRequest(nombreOtroEquipo, nombreEquipoVisitante, 500.00, horarioPartido.plusDays(7)), cargaDePartido)
        val partido2 = matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreOtroEquipo, 500.00, horarioPartido), cargaDePartido)

        val partidos = matchService.searchNextMatchsByPartialName("", null, horarioPartido.minusDays(5))

        assertThat(partidos[0]).isEqualTo(partido2)
        assertThat(partidos[1]).isEqualTo(partido1)
    }

    @Test
    fun `no se repiten los partidos en una busqueda si ambos equipos matchean con el nombre parcial buscado`() {
        val nombreDeUnEquipo = "fieles"
        teamService.registerTeam(CreateTeamRequest(nombreDeUnEquipo, "un apodo", "un estadio"))

        val partidoCreado = matchService.createMatch(CreateMatchRequest(nombreOtroEquipo, nombreDeUnEquipo, 500.00, horarioPartido), cargaDePartido)

        val partidos = matchService.searchNextMatchsByPartialName("ele", null, horarioPartido)

        assertThat(partidos.size).isEqualTo(1)
        assertThat(partidos).containsExactly(partidoCreado)
    }

    @Test
    fun `un equipo no puede jugar un partido si tiene un partido programado dentro de las setenta y dos horas anteriores`() {
        matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreEquipoVisitante, 500.00, horarioPartido), cargaDePartido)

        val excepcion = assertThrows<TeamNearlyPlayException> {
            matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreOtroEquipo, 500.00, horarioPartido.plusDays(3)), cargaDePartido)
        }

        assertThat(excepcion.message)
                .isEqualTo("$nombreEquipoLocal no puede jugar el 23/9/2021 porque tiene un partido el dia 20/9/2021")
    }

    @Test
    fun `un equipo no puede jugar un partido si tiene un partido programado dentro de las setenta y dos horas posteriores`() {
        matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreEquipoVisitante, 500.00, horarioPartido), cargaDePartido)

        val excepcion = assertThrows<TeamNearlyPlayException> {
            matchService.createMatch(CreateMatchRequest(nombreOtroEquipo, nombreEquipoVisitante, 500.00, horarioPartido.minusDays(3)), cargaDePartido)
        }

        assertThat(excepcion.message)
                .isEqualTo("$nombreEquipoVisitante no puede jugar el 17/9/2021 porque tiene un partido el dia 20/9/2021")
    }

    @Test
    fun `dos equipos no pueden jugar un partido con la misma condicion de local y visitante`() {
        matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreEquipoVisitante, 500.00, horarioPartido), cargaDePartido)

        val excepcion = assertThrows<MatchAlreadyExists> {
            matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreEquipoVisitante, 500.00, horarioPartido.plusMonths(3)), cargaDePartido)
        }

        assertThat(excepcion.message)
                .isEqualTo("Ya se ha disputado un partido entre $nombreEquipoLocal como local y $nombreEquipoVisitante como visitante")
    }

    @Test
    fun `se pueden pedir los datos de un partido`() {
        val partido = matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreEquipoVisitante, 500.00, horarioPartido), cargaDePartido)

        val datosDePartido = matchService.getMatchDetails(partido.id)

        assertThat(datosDePartido.home).isEqualTo(partido.home)
        assertThat(datosDePartido.away).isEqualTo(partido.away)
        assertThat(datosDePartido.matchStartTime).isEqualTo(partido.matchStartTime)
        assertThat(datosDePartido.ticketPrice).isEqualTo(partido.ticketPrice)
        assertThat(datosDePartido.stadium).isEqualTo(partido.stadium)
    }

    @Test
    fun `los partidos deben crearse con al menos siete dias de anticipacion`(){
        val excepcion = assertThrows<InvalidStartTimeException> {
            matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreEquipoVisitante, 500.00, horarioPartido), horarioPartido.minusDays(6))
        }

        assertThat(excepcion.message).isEqualTo("Los partidos tienen que crearse con al menos siete dias de anticipacion")
    }

    @Test
    fun `se pueden obtener los partidos de hoy`(){
        val unHorario = LocalDateTime.of(2021, 9, 20, 23, 59, 59)
        val partido = matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreEquipoVisitante, 500.00, unHorario), cargaDePartido)

        assertThat(matchService.todayMatchs(horarioPartido)).isEqualTo(listOf(partido))
    }

    @AfterEach
    fun tearDown() {
        matchService.clearDataSet()
    }
}