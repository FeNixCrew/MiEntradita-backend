package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.builders.SpectatorBuilder
import ar.edu.unq.mientradita.model.exception.*
import ar.edu.unq.mientradita.service.dto.CreateTeamRequest
import ar.edu.unq.mientradita.service.dto.TicketDTO
import ar.edu.unq.mientradita.service.dto.CreateMatchRequest
import ar.edu.unq.mientradita.service.dto.LoginRequest
import ar.edu.unq.mientradita.service.dto.UserDTO
import ar.edu.unq.mientradita.webservice.controllers.RegisterRequest
import ar.edu.unq.mientradita.service.dto.SuccessPaymentRequest

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
    val unEstadio = "un estadio"
    val unApodo = "un apodo"

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

        teamService.registerTeam(CreateTeamRequest(nombreEquipoLocal, unApodo, unEstadio, 20, 0.0, 0.0))
        teamService.registerTeam(CreateTeamRequest(nombreEquipoVisitante, unApodo, unEstadio, 20, 0.0, 0.0))
        teamService.registerTeam(CreateTeamRequest("Boca", unApodo, unEstadio, 20, 0.0, 0.0))
        teamService.registerTeam(CreateTeamRequest("Velez", unApodo, unEstadio, 20, 0.0, 0.0))

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

        val exception = assertThrows<AlreadyExistsException> {
            authUserService.createSpectator(
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
    fun `no se puede obtener informacion de un espectador si se introduce mal su nombre de usuario`() {
        val exception = assertThrows<BusinessException> { authUserService.login(LoginRequest("nico0510", "incorrecto")) }

        assertThat(exception.message).isEqualTo("Las credenciales introducidas son incorrectas, intente de nuevo")
    }

    @Test
    fun `no se puede obtener informacion de un espectador si se introduce mal su contrasenia`() {
        val exception = assertThrows<BusinessException> { authUserService.login(LoginRequest("malUsuario", "1234")) }

        assertThat(exception.message).isEqualTo("Las credenciales introducidas son incorrectas, intente de nuevo")
    }

    @Test
    fun `un espectador puede reservar una entrada`() {
        val partidoDTO = matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreEquipoVisitante, 500F, horarioPartido, 50), cargaDePartido)
        val entradaReservada = spectatorService.reserveTicket(espectador.id, partidoDTO.id, horarioPartido.minusDays(4))

        assertThat(entradaReservada)
                .isEqualTo(TicketDTO(entradaReservada.id, espectador.id, partidoDTO.id, nombreEquipoLocal, nombreEquipoVisitante, horarioPartido, 500F, entradaReservada.link, entradaReservada.isPaid))
    }

    @Test
    fun `un espectador no puede reservar una entrada para un partido que ya no tiene cupos`() {
        val espectador2 = authUserService.createSpectator(
                RegisterRequest(
                        name = "Fede",
                        surname = "Sandoval",
                        username = "fede0000",
                        password = "1234",
                        email = "fede0000@gmail.com",
                        dni = 1234567
                )
        )
        teamService.registerTeam(CreateTeamRequest("Equipo 1", unApodo, unEstadio, 2, 0.0, 0.0))
        val partidoDTO = matchService.createMatch(CreateMatchRequest("Equipo 1", nombreEquipoVisitante, 500F, horarioPartido, 50), cargaDePartido)
        spectatorService.reserveTicket(espectador2.id, partidoDTO.id, horarioPartido.minusDays(4))

        val exception = assertThrows<BusinessException> {
            spectatorService.reserveTicket(espectador.id, partidoDTO.id, horarioPartido.minusDays(4))
        }

        assertThat(exception.message).isEqualTo("Ya no hay entradas disponibles para este partido")
    }

    @Test
    fun `un espectador no puede reservar mas de una entrada del mismo partido`() {
        val partidoDTO = matchService.createMatch(CreateMatchRequest("Boca", "Velez", 500F, horarioPartido, 50), cargaDePartido)
        spectatorService.reserveTicket(espectador.id, partidoDTO.id, horarioPartido.minusDays(4))

        val exception = assertThrows<AlreadyExistsException> { spectatorService.reserveTicket(espectador.id, partidoDTO.id, horarioPartido.minusDays(2)) }

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
        val partido1 = matchService.createMatch(CreateMatchRequest("Boca", "Velez", 500F, horarioPartido, 50), cargaDePartido)
        val partido2 = matchService.createMatch(CreateMatchRequest(nombreEquipoVisitante, nombreEquipoLocal, 500F, horarioPartido.plusDays(5), 50), cargaDePartido)

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
        val partido1 = matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreEquipoVisitante, 500F, horarioPartido, 50), cargaDePartido)
        val partido2 = matchService.createMatch(CreateMatchRequest(nombreEquipoVisitante, nombreEquipoLocal, 500F, horarioPartido.plusDays(5), 50), cargaDePartido)

        val entrada1 = spectatorService.reserveTicket(espectadorDTO.id, partido1.id)
        val entrada2 = spectatorService.reserveTicket(espectadorDTO.id, partido2.id)

        spectatorService.savePaymentFrom(SuccessPaymentRequest(espectadorDTO.id, entrada1.id, "1243590211"))
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

        val partido = matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreEquipoVisitante, 500F, horarioPartido, 50), cargaDePartido)

        val obtainedFans = spectatorService.fansFrom(partido.id)
        assertThat(obtainedFans).hasSize(2)
        assertThat(obtainedFans[0].id).isEqualTo(espectador.id)
        assertThat(obtainedFans[1].id).isEqualTo(otroEspectador.id)
    }

    @Test
    fun `no se pueden obtener los fans de los equipos de un partido inexistente`() {
        val exception = assertThrows<MatchNotFoundException> {
            spectatorService.fansFrom(999999999)
        }

        assertThat(exception.message).isEqualTo("Partido no encontrado")
    }

    @Test
    fun `mi equipo favorito inicialmente no tiene ningun proximo partido`() {
        val team = teamService.getTeamDetails("Boca")
        spectatorService.markAsFavourite(espectador.id, team.id)

        assertThat(spectatorService.nextMatchesOfFavoriteTeam(espectador.id, horarioPartido)).isEmpty()
    }

    @Test
    fun `si un espectador no tiene equipo favorito, entonces no existen proximos partidos`() {
        teamService.getTeamDetails("Boca")

        assertThat(spectatorService.nextMatchesOfFavoriteTeam(espectador.id, horarioPartido)).isNull()
    }

    @Test
    fun `se pueden obtener los proximos partidos de mi equipo favorito`() {
        val team = teamService.getTeamDetails("Boca")
        spectatorService.markAsFavourite(espectador.id, team.id)
        val partido1 = matchService.createMatch(CreateMatchRequest("Boca", "racing", 500F, horarioPartido.plusDays(1), 50), cargaDePartido)
        val partido2 = matchService.createMatch(CreateMatchRequest("Velez", "Boca", 500F, horarioPartido.plusDays(7), 50), cargaDePartido)
        val expectedMatchs = arrayListOf(partido1, partido2)

        assertThat(spectatorService.nextMatchesOfFavoriteTeam(espectador.id, horarioPartido))
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("isReserved")
                .isEqualTo(expectedMatchs)
    }

    @Test
    fun `al consultar por los proximos partidos no se trae partidos viejos`() {
        val team = teamService.getTeamDetails("Boca")
        spectatorService.markAsFavourite(espectador.id, team.id)
        matchService.createMatch(CreateMatchRequest("Boca", "racing", 500F, horarioPartido.plusDays(1), 50), cargaDePartido)
        val partido2 = matchService.createMatch(CreateMatchRequest("Velez", "Boca", 500F, horarioPartido.plusDays(7), 50), cargaDePartido)
        val expectedMatchs = arrayListOf(partido2)

        assertThat(spectatorService.nextMatchesOfFavoriteTeam(espectador.id, horarioPartido.plusDays(2)))
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("isReserved")
                .isEqualTo(expectedMatchs)
    }

    @Test
    fun `se pueden obtener las entradas pendientes de pago de un espectador`() {
        val partidoDTO = matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreEquipoVisitante, 500F, horarioPartido, 50), cargaDePartido)

        val entradaReservada = spectatorService.reserveTicket(espectador.id, partidoDTO.id, horarioPartido.minusDays(4))

        assertThat(spectatorService.pendingTicketsPaymentFor(espectador.id))
            .containsExactly(entradaReservada)
    }

    @Test
    fun `se puede registrar un pago de entradas para un partido y deja de ser pendiente de pago`() {
        val partidoDTO = matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreEquipoVisitante, 500F, horarioPartido, 50), cargaDePartido)
        val entradaReservada = spectatorService.reserveTicket(espectador.id, partidoDTO.id, horarioPartido.minusDays(4))

        spectatorService.savePaymentFrom(SuccessPaymentRequest(espectador.id, entradaReservada.id, "1243590211"))

        assertThat(spectatorService.pendingTicketsPaymentFor(espectador.id)).isEmpty()
    }

    @Test
    fun `no se puede registrar un pago de entrada si la misma no es valida`() {
        val partidoDTO = matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreEquipoVisitante, 500F, horarioPartido, 50), cargaDePartido)
        val entradaReservada = spectatorService.reserveTicket(espectador.id, partidoDTO.id, horarioPartido.minusDays(4))

        val exception = assertThrows<BusinessException> {
            spectatorService.savePaymentFrom(SuccessPaymentRequest(espectador.id, entradaReservada.id, "999999999"))
        }

        assertThat(exception.message).isEqualTo("El id de pago no es valido")
    }

    @Test
    fun `no se puede registrar un pago de entrada si la misma no esta aprobada`() {
        val partidoDTO = matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreEquipoVisitante, 500F, horarioPartido, 50), cargaDePartido)
        val entradaReservada = spectatorService.reserveTicket(espectador.id, partidoDTO.id, horarioPartido.minusDays(4))

        val exception = assertThrows<BusinessException> {
            spectatorService.savePaymentFrom(SuccessPaymentRequest(espectador.id, entradaReservada.id, "1243732308"))
        }

        assertThat(exception.message).isEqualTo("El pago de la entrada no posee un estado de aprobado")
    }

    @Test
    fun `se puede registrar un pago de entradas para un partido y ahora esta paga`() {
        val partidoDTO = matchService.createMatch(CreateMatchRequest(nombreEquipoLocal, nombreEquipoVisitante, 500F, horarioPartido, 50), cargaDePartido)
        val entradaReservada = spectatorService.reserveTicket(espectador.id, partidoDTO.id, horarioPartido.minusDays(4))

        spectatorService.savePaymentFrom(SuccessPaymentRequest(espectador.id, entradaReservada.id, "1243590211"))

        val entradaDespuesDeSerPagada = spectatorService.pendingTickets(espectador.id, horarioPartido.minusDays(4)).first()

        assertThat(entradaDespuesDeSerPagada.isPaid).isTrue
    }

    @AfterEach
    fun tearDown() {
        matchService.clearDataSet()
    }
}