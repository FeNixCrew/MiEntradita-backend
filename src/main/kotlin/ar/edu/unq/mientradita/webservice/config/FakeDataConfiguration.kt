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
                createSpectators(spectatorService, matchService)
            }

    private fun createSpectators(spectatorService: SpectatorService, matchService: MatchService) {
        val matchs = generateMatchs(matchService)

        val pepe = spectatorService.createSpectator(
                "Pepe",
                "Argento",
                "pepito1299",
                "mariaenema1234",
                "zapatero_bigote@gmail.com",
                22334455)

        generateTicketsFor(pepe,matchs,spectatorService)

        val moniFutbolera = spectatorService.createSpectator(
                "Moni",
                "Argento",
                "holasusana9999",
                "cafecito0000",
                "aguante_el_sillon@gmail.com",
                224455770)

        generateTicketsFor(moniFutbolera, listOf(matchs.first(), matchs.last()),spectatorService)
    }

    private fun generateTicketsFor(spectator: Spectator, matchs: List<Match>, spectatorService: SpectatorService){
        val horaDeReserva = LocalDateTime.of(2021,9,16,10,0)
        matchs.forEach {
            spectatorService.reserveTicket(spectator.id!!,it.id!!, horaDeReserva)
        }
    }

    private fun generateMatchs(matchService: MatchService): List<Match>{
        val horaDelPartido1 = LocalDateTime.of(2021,9,18,16,0)
        val horaDelPartido2 = LocalDateTime.of(2021,9,18,21,0)
        val horaDelPartido3 = LocalDateTime.of(2021,9,19,16,0)
        val horaDelPartido4 = LocalDateTime.of(2021,9,19,21,0)

        val racingIndependiente = matchService.createMatch("Racing", "Independiente", 700.00,horaDelPartido1)
        val riverDefe = matchService.createMatch("River", "Defensa y Justicia", 500.00,horaDelPartido2)
        val colonEstudiantes = matchService.createMatch("Colon de Santa Fe", "Estudiantes de la Plata", 500.00,horaDelPartido3)
        val talleresArsenal = matchService.createMatch("Talleres de Cordoba", "Arsenal de Sarandi", 500.00,horaDelPartido4)

        return listOf(racingIndependiente,riverDefe,colonEstudiantes,talleresArsenal)
    }


}