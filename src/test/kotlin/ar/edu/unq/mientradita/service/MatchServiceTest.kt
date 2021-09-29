package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.exception.AlreadyPresentInGameException
import ar.edu.unq.mientradita.model.exception.MatchDoNotExistsException
import ar.edu.unq.mientradita.model.exception.SpectatorNotRegistered
import ar.edu.unq.mientradita.webservice.CreateMatchRequest
import ar.edu.unq.mientradita.webservice.RegisterRequest
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

    private val equipoLocal = "river"
    private val equipoVisitante = "racing"
    private val horarioPartido = LocalDateTime.of(2021, 9, 20, 16, 0)

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
    }

    @Test
    fun `se pueden crear partidos`() {
        val partidoDTO = matchService.createMatch(CreateMatchRequest(equipoLocal, equipoVisitante, 500.00, horarioPartido))

        assertThat(partidoDTO).isNotNull
    }

    @Test
    fun `al asistir a un partido se ve un mensaje de bienvenida`() {
        val partidoDTO = matchService.createMatch(CreateMatchRequest(equipoLocal, equipoVisitante, 500.00, horarioPartido))
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
        val partidoDTO = matchService.createMatch(CreateMatchRequest(equipoLocal, equipoVisitante, 500.00, horarioPartido))


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
        val partidoDTO = matchService.createMatch(CreateMatchRequest(equipoLocal, equipoVisitante, 500.00, horarioPartido))
        spectatorService.reserveTicket(espectador.id, partidoDTO.id, horarioPartido.minusDays(4))
        matchService.comeIn(espectador.id, partidoDTO.id, horarioPartido)

        val exception = assertThrows<AlreadyPresentInGameException> { matchService.comeIn(espectador.id, partidoDTO.id, horarioPartido) }

        assertThat(exception.message).isEqualTo("El espectador ya ha ingresado al partido")
    }

    @Test
    fun `se pueden buscar partidos proximos por matcheo de nombre de un equipo`() {
        val nombreEquipo = "velez"
        val partidosCreados = mutableListOf<MatchDTO>()
        partidosCreados.add(matchService.createMatch(CreateMatchRequest(nombreEquipo, equipoVisitante, 500.00, horarioPartido)))
        partidosCreados.add(matchService.createMatch(CreateMatchRequest(equipoLocal, nombreEquipo, 500.00, horarioPartido.plusDays(7))))

        val partidos = matchService.searchNextMatchsByPartialName("vel")

        assertThat(partidos.size).isEqualTo(2)
        assertThat(partidos).containsExactly(partidosCreados[0], partidosCreados[1])
    }

    @Test
    fun `no se repiten los partidos en una busqueda si ambos equipos matchean con el nombre parcial buscado`() {
        val nombreEquipo = "velez"
        val nombreEquipo2 = "fieles"

        val partidoCreado = matchService.createMatch(CreateMatchRequest(nombreEquipo, nombreEquipo2, 500.00, horarioPartido))

        val partidos = matchService.searchNextMatchsByPartialName("ele")

        assertThat(partidos.size).isEqualTo(1)
        assertThat(partidos).containsExactly(partidoCreado)
    }

    @AfterEach
    fun tearDown() {
        matchService.clearDataSet()
    }
}