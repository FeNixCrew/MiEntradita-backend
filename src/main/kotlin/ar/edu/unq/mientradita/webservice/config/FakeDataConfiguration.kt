package ar.edu.unq.mientradita.webservice.config

import ar.edu.unq.mientradita.model.user.Admin
import ar.edu.unq.mientradita.model.user.Scanner
import ar.edu.unq.mientradita.persistence.UserRepository
import ar.edu.unq.mientradita.service.*
import ar.edu.unq.mientradita.webservice.controllers.CreateMatchRequest
import ar.edu.unq.mientradita.webservice.controllers.RegisterRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime

@Configuration
class FakeDataConfiguration {

    @Autowired
    private lateinit var matchService: MatchService

    @Autowired
    private lateinit var spectatorService: SpectatorService

    @Autowired
    private lateinit var authUserService: AuthUserService

    @Autowired
    private lateinit var teamService: TeamService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Bean
    fun fakeDataInject() =
            CommandLineRunner {
                createSpectators()
            }

    private fun createSpectators() {
        val matchs = generateMatchs()

        userRepository.save(Admin("admin", "admin", "admin.mientradita2021@gmail.com"))
        userRepository.save(Scanner("scanner", "scanner", "scanner.mientradita2021@gmail.com"))

        val pepe = authUserService.createSpectator(
                RegisterRequest(
                        "Pepe",
                        "Argento",
                        "usuario",
                        "usuario",
                        22334455,
                        "pepito_argento@gmail.com")
        )

        generateTicketsFor(pepe.id, matchs, spectatorService)

        val moniFutbolera = authUserService.createSpectator(
                RegisterRequest(
                        "Moni",
                        "Argento",
                        "holasusana9999",
                        "cafecito000",
                        224455770,
                        "moni_argento@gmail.com")
        )

        generateTicketsFor(moniFutbolera.id, listOf(matchs.first(), matchs.last()), spectatorService)

        spectatorService.markAsFavourite(moniFutbolera.id, 1)
    }

    private fun generateTicketsFor(spectatorId: Long, matchs: List<MatchDTO>, spectatorService: SpectatorService) {
        matchs.forEach {
            spectatorService.reserveTicket(spectatorId, it.id)
        }
    }

    private fun generateMatchs(): List<MatchDTO> {
        val fechaDeAhora = LocalDateTime.now()
        val horaDelPartido1 = fechaDeAhora.plusHours(1)
        val horaDelPartido2 = fechaDeAhora.plusHours(3)
        val horaDelPartido3 = fechaDeAhora.plusDays(1)
        val horaDelPartido4 = fechaDeAhora.plusMonths(1)
        val horaDelPartido5 = fechaDeAhora.minusDays(5)
        val fechaCargaDePartido = fechaDeAhora.minusMonths(1)

        val racing = teamService.registerTeam(CreateTeamRequest("Racing", "Academia", "Estadio Presidente Peron", 42500))
        val independiente = teamService.registerTeam(CreateTeamRequest("Independiente", "Bojo", "Estadio Libertadores de América", 42069))
        val river = teamService.registerTeam(CreateTeamRequest("River", "Millonario", "Estadio Antonio Vespucio Liberti", 72054))
        val dyj = teamService.registerTeam(CreateTeamRequest("Defensa y Justicia", "El Halcon de Varela", "Estadio Norberto Tito Tomaghello", 10500))
        val colon = teamService.registerTeam(CreateTeamRequest("Colon de Santa Fe", "El Sabalero", "Estadio Brigadier General Estanislao López", 45000))
        val estudiantes = teamService.registerTeam(CreateTeamRequest("Estudiantes de la Plata", "Pincharratas", "Estadio UNO Jorge Luis Hirschi", 30018))
        val talleres = teamService.registerTeam(CreateTeamRequest("Talleres de Cordoba", "El Matador", "Estadio Mario Alberto Kempes", 57000))
        val arsenal = teamService.registerTeam(CreateTeamRequest("Arsenal de Sarandi", "El Arse", "Estadio Julio Humberto Grondona", 18300))
        val atlTucuman = teamService.registerTeam(CreateTeamRequest("Atletico Tucuman", "El Decano", "Estadio Monumental José Fierro", 35200))
        val gimnasiaLP = teamService.registerTeam(CreateTeamRequest("Gimnasia y Esgrima de la Plata", "El Lobo Tripero", "Estadio Juan Carmelo Zerillo", 21500))

        val racingIndependiente = matchService.createMatch(CreateMatchRequest(racing.name, independiente.name, 700.00, horaDelPartido1, 50), actualTime = fechaCargaDePartido)
        val riverDefe = matchService.createMatch(CreateMatchRequest(river.name, dyj.name, 500.00, horaDelPartido2, 50), actualTime = fechaCargaDePartido)
        val colonEstudiantes = matchService.createMatch(CreateMatchRequest(colon.name, estudiantes.name, 500.00, horaDelPartido3, 50), actualTime = fechaCargaDePartido)
        val talleresArsenal = matchService.createMatch(CreateMatchRequest(talleres.name, arsenal.name, 500.00, horaDelPartido4, 50), actualTime = fechaCargaDePartido)
        val atleticoTucumanGimnasia = matchService.createMatch(CreateMatchRequest(atlTucuman.name, gimnasiaLP.name, 400.00, horaDelPartido5, 50), actualTime = fechaCargaDePartido)

        return listOf(racingIndependiente, riverDefe, colonEstudiantes, talleresArsenal, atleticoTucumanGimnasia)
    }


}