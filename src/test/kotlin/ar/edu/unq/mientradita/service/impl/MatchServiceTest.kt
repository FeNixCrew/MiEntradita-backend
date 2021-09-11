package ar.edu.unq.mientradita.service.impl

import ar.edu.unq.mientradita.model.Attend
import ar.edu.unq.mientradita.model.Team
import ar.edu.unq.mientradita.persistence.MatchRepository
import ar.edu.unq.mientradita.service.MatchService
import ar.edu.unq.mientradita.service.SpectatorService
import ar.edu.unq.mientradita.service.TeamService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class MatchServiceTest {
    @Autowired
    private lateinit var matchService: MatchService
    @Autowired
    private lateinit var teamService : TeamService
    @Autowired
    private lateinit var spectatorService: SpectatorService

    private val horarioPartido = LocalDateTime.of(2021, 9, 20, 16, 0)
    private lateinit var equipoLocal: Team
    private lateinit var equipoVisitante: Team

    @Test
    fun `se pueden guardar partidos`() {
        equipoLocal = teamService.registerTeam(
            "racing",
            "el cilindro",
            "Pte Peron",
            100,
            "Avellaneda",
            -34.667737,
            -58.3682195
        )

        equipoVisitante = teamService.registerTeam(
            "river",
            "el monumental",
            "Antonio Vespucio",
            150,
            "Nuniez",
            -32.667737,
            -57.3682195
        )

        val match =  matchService.createMatch(equipoLocal.id!!, equipoVisitante.id!!, 500.00, horarioPartido)

        assertThat(match).usingRecursiveComparison().isEqualTo(matchService.findMatchBy(match.id!!))
    }

    @Test
    fun `un espectador asiste a un partido`(){
        equipoLocal = teamService.registerTeam(
                "racing",
                "el cilindro",
                "Pte Peron",
                100,
                "Avellaneda",
                -34.667737,
                -58.3682195
        )

        equipoVisitante = teamService.registerTeam(
                "river",
                "el monumental",
                "Antonio Vespucio",
                150,
                "Nuniez",
                -32.667737,
                -57.3682195
        )

        val espectador = spectatorService.createSpectator(
                name = "Nicolas",
                surname = "Martinez",
                username = "nico0510",
                password = "1234",
                email = "nico0510@gmail.com",
                dni = 12345678
        )
        val match = matchService.createMatch(equipoLocal.id!!, equipoVisitante.id!!, 500.00, horarioPartido)
        spectatorService.reserveTicket(match.id!!,espectador.id!!, horarioPartido.minusDays(4))

        matchService.comeIn(match.id!!, espectador.id!!, horarioPartido)

        val ticketDespuesDeAsistirAlPartido = spectatorService.findTicketFrom(espectador.id!!, match.id!!)
        assertThat(ticketDespuesDeAsistirAlPartido.state).isEqualTo(Attend.PRESENT)
    }
}