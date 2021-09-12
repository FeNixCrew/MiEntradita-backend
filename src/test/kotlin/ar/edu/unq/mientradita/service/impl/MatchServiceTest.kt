package ar.edu.unq.mientradita.service.impl

import ar.edu.unq.mientradita.model.Attend
import ar.edu.unq.mientradita.service.MatchService
import ar.edu.unq.mientradita.service.SpectatorService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class MatchServiceTest {
    @Autowired
    private lateinit var matchService: MatchService
    @Autowired
    private lateinit var spectatorService: SpectatorService

    private val equipoLocal = "river"
    private val equipoVisitante = "racing"
    private val horarioPartido = LocalDateTime.of(2021, 9, 20, 16, 0)

    @Test
    fun `se pueden guardar partidos`() {
        val partido =  matchService.createMatch(equipoLocal, equipoVisitante, 500.00, horarioPartido)

        assertThat(partido).usingRecursiveComparison().isEqualTo(matchService.findMatchBy(partido.id!!))
    }

    @Test
    fun `un espectador asiste a un partido`(){
        val espectador = spectatorService.createSpectator(
                name = "Nicolas",
                surname = "Martinez",
                username = "nico0510",
                password = "1234",
                email = "nico0510@gmail.com",
                dni = 12345678
        )
        val partido = matchService.createMatch(equipoLocal, equipoVisitante, 500.00, horarioPartido)
        spectatorService.reserveTicket(espectador.id!!, partido.id!!,horarioPartido.minusDays(4))

        matchService.comeIn(espectador.id!!, partido.id!!, horarioPartido)

        val ticketDespuesDeAsistirAlPartido = spectatorService.findTicketFrom(espectador.id!!, partido.id!!)
        assertThat(ticketDespuesDeAsistirAlPartido.state).isEqualTo(Attend.PRESENT)
    }
}