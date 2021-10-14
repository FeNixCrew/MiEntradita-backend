package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.exception.MatchDoNotExistsException
import ar.edu.unq.mientradita.model.exception.TeamAlredyRegisteredException
import ar.edu.unq.mientradita.webservice.CreateMatchRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TeamServiceTest {

    @Autowired
    private lateinit var teamService: TeamService

    @Autowired
    private lateinit var matchService: MatchService

    @Test
    fun `se pueden registrar equipos`() {
        val equipoDTO = teamService.registerTeam(CreateTeamRequest("River Plate", "El Millo", "El Monumental"))

        assertThat(equipoDTO).isNotNull
    }

    @Test
    fun `no se pueden registrar dos veces a un equipo`() {
        teamService.registerTeam(CreateTeamRequest("River Plate", "El Millo", "El Monumental"))
        val exception = assertThrows<TeamAlredyRegisteredException> {
            teamService.registerTeam(CreateTeamRequest("River Plate", "El Millo", "El Monumental"))
        }

        assertThat(exception.message).isEqualTo("El equipo ya fue registrado")
    }

    @Test
    fun `se pueden obtener todos los equipos`() {
        val river = teamService.registerTeam(CreateTeamRequest("River Plate", "El Millo", "El Monumental"))
        val racing = teamService.registerTeam(CreateTeamRequest("Racing Club", "La Academia", "El Cilindro"))

        val equiposEsperados = listOf(river.name, racing.name).map { TeamDTO(it) }
        assertThat(teamService.getTeams()).usingRecursiveComparison().isEqualTo(equiposEsperados)
    }

    @AfterEach
    fun tearDown() {
        teamService.clearDataSet()
    }

}