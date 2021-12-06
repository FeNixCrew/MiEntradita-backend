package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.exception.AlreadyExistsException
import ar.edu.unq.mientradita.service.dto.CreateTeamRequest
import ar.edu.unq.mientradita.service.dto.TeamDTO
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
        equipo = teamService.registerTeam(CreateTeamRequest("River", "El Millo", "El Monumental", 200, 0.0, 0.0))
    }

    @Test
    fun `se pueden registrar equipos`() {
        assertThat(equipo.id).isNotNull
    }

    @Test
    fun `no se pueden registrar a un equipo con un nombre ya existente`() {
        val exception = assertThrows<AlreadyExistsException> {
            teamService.registerTeam(CreateTeamRequest("River", "El Millo", "El Monumental", 200, 0.0, 0.0))
        }

        assertThat(exception.message).isEqualTo("El equipo ya fue registrado")
    }

    @Test
    fun `se pueden obtener todos los equipos`() {
        val racing = teamService.registerTeam(CreateTeamRequest("Racing", "La Academia", "El Cilindro", 200, 0.0, 0.0))

        val equiposEsperados = listOf(equipo, racing).map { TeamDTO(it.id, it.name, it.knowName, it.stadium, it.stadiumCapacity, it.stadiumLatitude, it.stadiumLongitude) }
        assertThat(teamService.getTeams()).usingRecursiveComparison().isEqualTo(equiposEsperados)
    }

    @BeforeEach
    fun tearDown() {
        matchService.clearDataSet()
    }
}