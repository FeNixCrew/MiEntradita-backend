package ar.edu.unq.mientradita.service.impl

import ar.edu.unq.mientradita.model.Team
import ar.edu.unq.mientradita.service.TeamService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TeamServiceTest {

    @Autowired
    private lateinit var teamService: TeamService

    private lateinit var equipo: Team

    @Test
    fun `se pueden guardar equipos`() {
        equipo = teamService.registerTeam(
            "racing",
            "el cilindro",
            "Pte Peron",
            100,
            "Avellaneda",
            -34.667737,
            -58.3682195
        )

        assertThat(equipo).usingRecursiveComparison().isEqualTo(teamService.findTeamBy(equipo.id!!))
    }
}