package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.exception.TeamAlredyRegisteredException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
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

    private lateinit var equipo: TeamDTO

    @BeforeEach
    fun setUp() {
        equipo = teamService.registerTeam(CreateTeamRequest("River", "El Millo", "El Monumental"))
    }

    @Test
    fun `se pueden registrar equipos`() {
        assertThat(equipo.id).isNotNull
    }

    @Test
    fun `no se pueden registrar a un equipo con un nombre ya existente`() {
        val exception = assertThrows<TeamAlredyRegisteredException> {
            teamService.registerTeam(CreateTeamRequest("River", "El Millo", "El Monumental"))
        }

        assertThat(exception.message).isEqualTo("El equipo ya fue registrado")
    }

    @Test
    fun `se pueden obtener todos los equipos`() {
        val racing = teamService.registerTeam(CreateTeamRequest("Racing", "La Academia", "El Cilindro"))

        val equiposEsperados = listOf(equipo, racing).map { TeamDTO(it.id, it.name, it.knowName, it.stadium) }
        assertThat(teamService.getTeams()).usingRecursiveComparison().isEqualTo(equiposEsperados)
    }

    @BeforeEach
    fun tearDown() {
        matchService.clearDataSet()
    }
}