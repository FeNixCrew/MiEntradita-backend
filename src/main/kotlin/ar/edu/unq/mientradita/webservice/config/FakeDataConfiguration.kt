package ar.edu.unq.mientradita.webservice.config

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.Spectator
import ar.edu.unq.mientradita.service.MatchDTO
import ar.edu.unq.mientradita.service.MatchService
import ar.edu.unq.mientradita.service.SpectatorService
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

    @Bean
    fun fakeDataInject() =
            CommandLineRunner {
                createSpectators()
            }

    private fun createSpectators() {
        val matchs = generateMatchs()

        spectatorService.createSpectator(
                RegisterRequest(
                        "scanner",
                        "scanner",
                        "scanner",
                        "scanner",
                        212837645,
                        "scanner@gmail.com",
                )
        )

        val pepe = spectatorService.createSpectator(
                RegisterRequest(
                        "Pepe",
                        "Argento",
                        "usuario",
                        "usuario",
                        22334455,
                        "zapatero_bigote@gmail.com")
        )

        generateTicketsFor(pepe.id, matchs, spectatorService)

        val moniFutbolera = spectatorService.createSpectator(
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
        val horaDelPartido1 = LocalDateTime.of(2021, 9, 26, 16, 0)
        val horaDelPartido2 = LocalDateTime.of(2021, 9, 25, 21, 0)
        val horaDelPartido3 = LocalDateTime.of(2021, 9, 23, 16, 0)
        val horaDelPartido4 = LocalDateTime.now().plusHours(1)
        val horaDelPartido5 = LocalDateTime.now().plusHours(3)

        val racingIndependiente = matchService.createMatch(CreateMatchRequest("Racing", "Independiente", 700.00, horaDelPartido1))
        val riverDefe = matchService.createMatch(CreateMatchRequest("River", "Defensa y Justicia", 500.00, horaDelPartido2))
        val colonEstudiantes = matchService.createMatch(CreateMatchRequest("Colon de Santa Fe", "Estudiantes de la Plata", 500.00, horaDelPartido3))
        val talleresArsenal = matchService.createMatch(CreateMatchRequest("Talleres de Cordoba", "Arsenal de Sarandi", 500.00, horaDelPartido4))
        val atleticoTucumanGimnasia = matchService.createMatch(CreateMatchRequest("Atletico Tucuman", "Gimnasia y Esgrima de la Plata", 400.00, horaDelPartido5))

        return listOf(racingIndependiente, riverDefe, colonEstudiantes, talleresArsenal, atleticoTucumanGimnasia)
    }


}