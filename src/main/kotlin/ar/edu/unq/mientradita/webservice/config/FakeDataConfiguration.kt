package ar.edu.unq.mientradita.webservice.config

import ar.edu.unq.mientradita.model.user.Admin
import ar.edu.unq.mientradita.model.user.Scanner
import ar.edu.unq.mientradita.persistence.UserRepository
import ar.edu.unq.mientradita.service.*
import ar.edu.unq.mientradita.webservice.CreateMatchRequest
import ar.edu.unq.mientradita.webservice.RegisterRequest
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
                        "zapatero_bigote@gmail.com")
        )

        generateTicketsFor(pepe.id, matchs, spectatorService)

        val moniFutbolera = authUserService.createSpectator(
                RegisterRequest(
                        "Moni",
                        "Argento",
                        "holasusana9999",
                        "cafecito000",
                        224455770,
                        "aguante_el_sillon@gmail.com")
        )

        generateTicketsFor(moniFutbolera.id, listOf(matchs.first(), matchs.last()), spectatorService)
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
        val horaDelPartido3 = fechaDeAhora.plusDays(5)
        val horaDelPartido4 = fechaDeAhora.plusMonths(1)
        val horaDelPartido5 = fechaDeAhora.minusDays(5)
        val fechaCargaDePartido = fechaDeAhora.minusMonths(1)

        val racing = teamService.registerTeam(CreateTeamRequest("Racing", "la Academia", "El Cilindro"))
        val independiente = teamService.registerTeam(CreateTeamRequest("Independiente", "el Rojo", "Estadio Libertadores de América"))
        val river = teamService.registerTeam(CreateTeamRequest("River", "el Millonario", "Estadio Antonio Vespucio Liberti"))
        val dyj = teamService.registerTeam(CreateTeamRequest("Defensa y Justicia", "el Halcon de Varela", "Estadio Norberto Tito Tomaghello"))
        val colon = teamService.registerTeam(CreateTeamRequest("Colon de Santa Fe", "el Sabalero", "Estadio Brigadier General Estanislao López"))
        val estudiantes = teamService.registerTeam(CreateTeamRequest("Estudiantes de la Plata", "el Pincharrata", "Estadio Unico de la Plata"))
        val talleres = teamService.registerTeam(CreateTeamRequest("Talleres de Cordoba", "el Matador", "Estadio Mario Alberto Kempes"))
        val arsenal = teamService.registerTeam(CreateTeamRequest("Arsenal de Sarandi", "el Arse", "Estadio Julio Humberto Grondona"))
        val atlTucuman = teamService.registerTeam(CreateTeamRequest("Atletico Tucuman", "el Decano", "Estadio Monumental José Fierro"))
        val gimnasiaLP = teamService.registerTeam(CreateTeamRequest("Gimnasia y Esgrima de la Plata", "el Lobo Tripero", "Estadio Juan Carmelo Zerillo"))

        val racingIndependiente = matchService.createMatch(CreateMatchRequest(racing.name, independiente.name, 700.00, horaDelPartido1, "El Cilindro"), actualTime = fechaCargaDePartido)
        val riverDefe = matchService.createMatch(CreateMatchRequest(river.name, dyj.name, 500.00, horaDelPartido2, "Estadio Antonio Vespucio Liberti"), actualTime = fechaCargaDePartido)
        val colonEstudiantes = matchService.createMatch(CreateMatchRequest(colon.name, estudiantes.name, 500.00, horaDelPartido3, "Estadio Brigadier General Estanislao López"), actualTime = fechaCargaDePartido)
        val talleresArsenal = matchService.createMatch(CreateMatchRequest(talleres.name, arsenal.name, 500.00, horaDelPartido4, "Estadio Mario Alberto Kempes"), actualTime = fechaCargaDePartido)
        val atleticoTucumanGimnasia = matchService.createMatch(CreateMatchRequest(atlTucuman.name, gimnasiaLP.name, 400.00, horaDelPartido5, "Estadio Monumental José Fierro"), actualTime = fechaCargaDePartido)

        return listOf(racingIndependiente, riverDefe, colonEstudiantes, talleresArsenal, atleticoTucumanGimnasia)
    }


}