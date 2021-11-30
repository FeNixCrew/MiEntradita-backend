package ar.edu.unq.mientradita.webservice.config

import ar.edu.unq.mientradita.model.user.Admin
import ar.edu.unq.mientradita.model.user.Scanner
import ar.edu.unq.mientradita.persistence.UserRepository
import ar.edu.unq.mientradita.service.AuthUserService
import ar.edu.unq.mientradita.service.MatchService
import ar.edu.unq.mientradita.service.SpectatorService
import ar.edu.unq.mientradita.service.TeamService
import ar.edu.unq.mientradita.service.dto.*
import ar.edu.unq.mientradita.webservice.controllers.RegisterRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder
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

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Bean
    fun fakeDataInject() =
            CommandLineRunner {
                createSpectators()
            }

    private fun createSpectators() {
        val matchs = generateMatchs()

        userRepository.save(Admin("admin", passwordEncoder.encode("admin"), "admin.mientradita2021@gmail.com"))
        userRepository.save(Scanner("scanner", passwordEncoder.encode("scanner"), "scanner.mientradita2021@gmail.com"))

        val pepe = authUserService.createSpectator(
                RegisterRequest(
                        "Pepe",
                        "Argento",
                        "usuario",
                        "usuario",
                        22334455,
                        "pepito_argento@gmail.com"
                )
        )

        val pepeEntradas = generateTicketsFor(pepe.id, matchs, spectatorService)

        val moniFutbolera = authUserService.createSpectator(
                RegisterRequest(
                        "Moni",
                        "Argento",
                        "holasusana9999",
                        "cafecito000",
                        22445570,
                        "moni_argento@gmail.com"
                )
        )

        generateTicketsFor(moniFutbolera.id, listOf(matchs.first()), spectatorService)

        spectatorService.markAsFavourite(moniFutbolera.id, 1)

        authUserService.createSpectator(
                RegisterRequest(
                        "Coqui",
                        "Argento",
                        "coqui00",
                        "usuario",
                        42229801,
                        "coqui_argento@gmail.com"
                )
        )
        var contador = 1
        val losJuanes = mutableListOf<UserDTO>()
        val partido = matchs.last()
        repeat(8) {
            val spectator = authUserService.createSpectator(
                    RegisterRequest(
                            "Juan${contador}",
                            "Gonzales${contador}",
                            "juancho00${contador}",
                            "usuario",
                            32298015 + contador,
                            "juan_gonzales${contador}@gmail.com"
                    )
            )
            losJuanes.add(spectator)
            contador += 1
            val entrada = spectatorService.reserveTicket(spectator.id, partido.id)
            spectatorService.savePaymentFrom(SuccessPaymentRequest(spectator.id, entrada.id, "1243590211"))
        }

        spectatorService.savePaymentFrom(SuccessPaymentRequest(pepe.id, pepeEntradas.last().id, "1243590211"))

        matchService.comeIn(pepe.id, partido.id)
        matchService.comeIn(losJuanes.first().id, partido.id)
        matchService.comeIn(losJuanes.last().id, partido.id)
    }

    private fun generateTicketsFor(spectatorId: Long, matchs: List<MatchDTO>, spectatorService: SpectatorService): List<TicketDTO> {
        return matchs.map {
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
        val horaDelPartido6 = fechaDeAhora.minusMinutes(1)
        val fechaCargaDePartido = fechaDeAhora.minusMonths(1)

        val racing = teamService.registerTeam(
                CreateTeamRequest(
                        "Racing",
                        "La Academia",
                        "Estadio Presidente Perón",
                        42500,
                        -34.6675,
                        -58.368611)
        )
        val independiente = teamService.registerTeam(
                CreateTeamRequest(
                        "Independiente",
                        "El Rojo",
                        "Estadio Libertadores de América",
                        42069,
                        -34.670267,
                        -58.370969
                )
        )
        val river = teamService.registerTeam(
                CreateTeamRequest(
                        "River",
                        "El Millonario",
                        "Estadio Antonio Vespucio Liberti",
                        72054,
                        -34.545278,
                        -58.449444
                )
        )
        val dyj = teamService.registerTeam(
                CreateTeamRequest(
                        "Defensa y Justicia",
                        "El Halcón de Varela",
                        "Estadio Norberto Tito Tomaghello",
                        10500,
                        -34.821753,
                        -58.286511
                )
        )
        val colon = teamService.registerTeam(
                CreateTeamRequest(
                        "Colón de Santa Fe",
                        "El Sabalero",
                        "Estadio Brigadier General Estanislao López",
                        45000,
                        -31.663217,
                        -60.725394
                )
        )
        val estudiantes = teamService.registerTeam(
                CreateTeamRequest(
                        "Estudiantes de la Plata",
                        "El Pincharrata",
                        "Estadio UNO Jorge Luis Hirschi",
                        30018,
                        -34.911925,
                        -57.938794
                )
        )
        val talleres = teamService.registerTeam(
                CreateTeamRequest(
                        "Talleres de Córdoba",
                        "El Matador",
                        "Estadio Mario Alberto Kémpes",
                        57000,
                        -31.368956,
                        -64.246244
                )
        )
        val arsenal = teamService.registerTeam(
                CreateTeamRequest(
                        "Arsenal de Sarandí",
                        "El Arse",
                        "Estadio Julio Humberto Grondona",
                        18300,
                        -34.678333,
                        -58.340278
                )
        )
        val atlTucuman = teamService.registerTeam(
                CreateTeamRequest(
                        "Atlético Tucumán",
                        "El Decano",
                        "Estadio Monumental José Fierro",
                        35200,
                        -26.812778,
                        -65.199167
                )
        )
        val gimnasiaLP = teamService.registerTeam(
                CreateTeamRequest(
                        "Gimnasia y Esgrima de la Plata",
                        "El Lobo Tripero",
                        "Estadio Juan Carmelo Zerillo",
                        21500,
                        -34.910975,
                        -57.932592
                )
        )
        val godoyCruz = teamService.registerTeam(
                CreateTeamRequest(
                        "Godoy Cruz",
                        "El Tomba",
                        "Estadio Malvinas Argentinas",
                        100,
                        -32.889564,
                        -68.879994
                )
        )


        val racingIndependiente = matchService.createMatch(
                CreateMatchRequest(racing.name, independiente.name, 700F, horaDelPartido1, 50),
                actualTime = fechaCargaDePartido
        )
        val riverDefe = matchService.createMatch(
                CreateMatchRequest(river.name, dyj.name, 500F, horaDelPartido2, 50),
                actualTime = fechaCargaDePartido
        )
        val colonEstudiantes = matchService.createMatch(
                CreateMatchRequest(colon.name, estudiantes.name, 500F, horaDelPartido3, 50),
                actualTime = fechaCargaDePartido
        )
        val talleresArsenal = matchService.createMatch(
                CreateMatchRequest(talleres.name, arsenal.name, 500F, horaDelPartido4, 50),
                actualTime = fechaCargaDePartido
        )
        val atleticoTucumanGimnasia = matchService.createMatch(
                CreateMatchRequest(atlTucuman.name, gimnasiaLP.name, 400F, horaDelPartido5, 50),
                actualTime = fechaCargaDePartido
        )
        val godoyCruzTalleres = matchService.createMatch(
                CreateMatchRequest(godoyCruz.name, talleres.name, 800F, horaDelPartido6, 11),
                actualTime = fechaCargaDePartido
        )

        return listOf(
                racingIndependiente,
                riverDefe,
                colonEstudiantes,
                talleresArsenal,
                atleticoTucumanGimnasia,
                godoyCruzTalleres
        )
    }


}
