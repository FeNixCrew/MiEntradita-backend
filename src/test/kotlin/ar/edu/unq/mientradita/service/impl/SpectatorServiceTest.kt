package ar.edu.unq.mientradita.service.impl

import ar.edu.unq.mientradita.service.MatchService
import ar.edu.unq.mientradita.service.SpectatorService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class SpectatorServiceTest {

    @Autowired
    private lateinit var spectatorService: SpectatorService

    @Autowired
    private lateinit var matchService: MatchService

    private val horarioPartido = LocalDateTime.of(2021, 10, 20, 16, 0)
    private var equipoVisitante = "river"
    private var equipoLocal = "racing"

    @Test
    fun `se puede crear un nuevo espectador`() {
        val espectador = spectatorService.createSpectator(
                name = "Nicolas",
                surname = "Martinez",
                username = "nico0510",
                password = "1234",
                email = "nico0510@gmail.com",
                dni = 12345678
        )
        val espectadorEsperado = spectatorService.findSpectatorById(espectador.id!!)

        assertThat(espectador)
            .usingRecursiveComparison()
            .ignoringFields("favoriteTeams", "tickets")
            .isEqualTo(espectadorEsperado)
    }

    @Test
    fun `un espectador puede reservar un ticket`() {
        val espectadorAntesDeReservarTicket = spectatorService.createSpectator(
                name = "Nicolas",
                surname = "Martinez",
                username = "nico0510",
                password = "1234",
                email = "nico0510@gmail.com",
                dni = 12345678
        )

        val partido = matchService.createMatch(equipoLocal, equipoVisitante, 500.00, horarioPartido)

        val espectadorDespuesDeReservarTicket = spectatorService.reserveTicket(espectadorAntesDeReservarTicket.id!!, partido.id!!, horarioPartido.minusDays(4))

        assertThat(espectadorAntesDeReservarTicket.haveTickets()).isFalse
        assertThat(espectadorDespuesDeReservarTicket.haveTickets()).isTrue
    }


}