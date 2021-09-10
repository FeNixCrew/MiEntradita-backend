package ar.edu.unq.mientradita.model

import ar.edu.unq.mientradita.model.builders.*
import ar.edu.unq.mientradita.model.exception.TicketFromMatchNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SpectatorTest {
    lateinit var espectador: Spectator
    lateinit var defe: Team
    lateinit var estadio: Stadium
    lateinit var partido: Match



    @BeforeEach
    fun setUp() {
        estadio = StadiumBuilder()
                .withKnownName("El Tito Tomaghello")
                .withRealName("Estadio Norberto Tomaghello")
                .withCapacity(10500)
                .withLocation(LocationBuilder()
                        .withLocation("Av. Humahuaca 244, Santa Rosa, Provincia de Buenos Aires")
                        .withLatitude(-34.821753)
                        .withLongitude(-58.286511)
                        .build())
                .build()

        defe = TeamBuilder().withName("Defensa y Justicia").withStadium(estadio).build()

        espectador = SpectatorBuilder()
                .withUsername("nico0510")
                .withPassword("1234")
                .withName("Nicolas")
                .withSurname("Martinez")
                .withEmail("nico@gmail.com")
                .withDni(42254396)
                .build()

        partido = MatchBuilder().withAvailableTickets(1).build()
    }

    @Test
    fun `un espectador conoce su informacion`() {
        val espectadorEsperado = Spectator("Martinez", "nico0510", "Nicolas", "nico@gmail.com", 42254396, "1234")
        assertThat(espectador).usingRecursiveComparison().isEqualTo(espectadorEsperado)

    }

    @Test
    fun `un espectador inicialemente no tiene equipos marcados como favoritos`() {
        assertThat(espectador.haveAFavoriteTeam()).isFalse
    }

    @Test
    fun `un espectador se agrega un equipo favorito y ahora tiene uno`() {
        espectador.addFavoriteTeam(defe)

        assertThat(espectador.haveAFavoriteTeam()).isTrue
        assertThat(espectador.myFavoriteTeams().size).isEqualTo(1)

    }

    @Test
    fun `un espectador inicialmente no tiene ninguna entrada reservada`(){
        assertThat(espectador.myTickets().size).isEqualTo(0)
        assertThat(espectador.haveTickets()).isFalse
    }

    @Test
    fun `un espectador reserva una entrada para un partido y ahora tiene una`(){
        espectador.reserveATicketFor(partido)

        assertThat(espectador.haveTickets()).isTrue
        assertThat(espectador.myTickets().size).isEqualTo(1)
    }

    @Test
    fun `no se le puede pedir a un espectador la entrada de un partido al cual no ha reservado`(){
        partido = MatchBuilder()
            .withHome(TeamBuilder().withName("Tigre").build())
            .withAway(TeamBuilder().withName("River").build())
            .build()
        espectador = SpectatorBuilder().withName("Nico").withSurname("Martinez").build()

        val excepcion = assertThrows<TicketFromMatchNotFoundException> { espectador.findTicketFrom(partido) }

        assertThat(excepcion.message)
            .isEqualTo("No se ha encontrado una entrada reservada de Nico Martinez para el partido Tigre vs River")
    }

    @Test
    fun `un espectador sabe encontrar una entrada de un partido que ha reservado`(){
        partido = MatchBuilder()
            .withHome(TeamBuilder().withName("Tigre").build())
            .withAway(TeamBuilder().withName("River").build())
            .withAvailableTickets(1)
            .build()
        espectador = SpectatorBuilder().withName("Nico").withSurname("Martinez").build()

        espectador.reserveATicketFor(partido)

        assertThat(espectador.findTicketFrom(partido).match).isEqualTo(partido)
    }
}