package ar.edu.unq.mientradita.webservice.config

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.Spectator
import ar.edu.unq.mientradita.service.MatchService
import ar.edu.unq.mientradita.service.SpectatorService
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
            "scanner",
            "scanner",
            "scanner",
            "scanner",
            "scanner@gmail.com",
            212837645
        )

        val pepe = spectatorService.createSpectator(
                "Pepe",
                "Argento",
                "usuario",
                "usuario",
                "zapatero_bigote@gmail.com",
                22334455)

        generateTicketsFor(pepe,matchs,spectatorService)

        val moniFutbolera = spectatorService.createSpectator(
                "Moni",
                "Argento",
                "holasusana9999",
                "cafecito000",
                "aguante_el_sillon@gmail.com",
                224455770)

        generateTicketsFor(moniFutbolera, listOf(matchs.first(), matchs.last()),spectatorService)
    }

    private fun generateTicketsFor(spectator: Spectator, matchs: List<Match>, spectatorService: SpectatorService){
        matchs.forEach {
            spectatorService.reserveTicket(spectator.id!!,it.id!!)
        }
    }

    private fun generateMatchs(): List<Match>{
        val horaDelPartido1 = LocalDateTime.of(2021,9,26,16,0)
        val horaDelPartido2 = LocalDateTime.of(2021,9,25,21,0)
        val horaDelPartido3 = LocalDateTime.of(2021,9,23,16,0)
        val horaDelPartido4 = LocalDateTime.now().plusHours(1)
        val horaDelPartido5 = LocalDateTime.now().plusHours(3)

        val racingIndependiente = matchService.createMatch("Racing", "Independiente", 700.00,horaDelPartido1)
        val riverDefe = matchService.createMatch("River", "Defensa y Justicia", 500.00,horaDelPartido2)
        val colonEstudiantes = matchService.createMatch("Colon de Santa Fe", "Estudiantes de la Plata", 500.00,horaDelPartido3)
        val talleresArsenal = matchService.createMatch("Talleres de Cordoba", "Arsenal de Sarandi", 500.00,horaDelPartido4)
        val atleticoTucumanGimnasia = matchService.createMatch("Atletico Tucuman", "Gimnasia y Esgrima de la Plata", 400.00,horaDelPartido5)

        return listOf(racingIndependiente,riverDefe,colonEstudiantes,talleresArsenal, atleticoTucumanGimnasia)
    }


}