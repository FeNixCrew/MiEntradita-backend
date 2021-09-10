package ar.edu.unq.mientradita.service.impl

import ar.edu.unq.mientradita.model.Team
import ar.edu.unq.mientradita.service.MatchService
import ar.edu.unq.mientradita.service.SpectatorService
import ar.edu.unq.mientradita.service.TeamService
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

    @Autowired
    private lateinit var teamService: TeamService

    private lateinit var equipoVisitante: Team
    private val horarioPartido = LocalDateTime.of(2021, 10, 20, 16, 0)
    private lateinit var equipoLocal: Team

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

        // TODO: revisar test despues de arrglar el todo de espectador
        assertThat(espectador).usingRecursiveComparison().ignoringFields("favoriteTeams").isEqualTo(espectadorEsperado)
    }

    @Test
    fun `un espectador puede reservar un ticket`() {
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

        val espectadorAntesDeReservarTicket = spectatorService.createSpectator(
                name = "Nicolas",
                surname = "Martinez",
                username = "nico0510",
                password = "1234",
                email = "nico0510@gmail.com",
                dni = 12345678
        )

        val match = matchService.createMatch(equipoLocal.id!!, equipoVisitante.id!!, 500.00, horarioPartido)

        val espectadorDespuesDeReservarTicket = spectatorService.reserveTicket(match.id!!, espectadorAntesDeReservarTicket.id!!, LocalDateTime.now())

        assertThat(espectadorAntesDeReservarTicket.haveTickets()).isFalse
        assertThat(espectadorDespuesDeReservarTicket.haveTickets()).isTrue

    }


}