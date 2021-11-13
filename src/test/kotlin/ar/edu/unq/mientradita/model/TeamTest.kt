package ar.edu.unq.mientradita.model

import ar.edu.unq.mientradita.model.builders.TeamBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TeamTest {

    @Test
    fun `un equipo inicialmente tiene un nombre`(){
        val equipo = TeamBuilder().withName("River Plate").build()

        assertThat(equipo.name).isEqualTo("River Plate")
    }

    @Test
    fun `un equipo tiene un nombre conocido`(){
        val equipo = TeamBuilder().withKnowName("El Millonario").build()

        assertThat(equipo.knowName).isEqualTo("El Millonario")
    }

    @Test
    fun `un equipo tiene un estadio`(){
        val equipo = TeamBuilder().withStadium("El Monumental").build()

        assertThat(equipo.stadium).isEqualTo("El Monumental")
    }

    @Test
    fun `un equipo tiene una capacidad maxima para albergar gente en su estadio`() {
        val equipo = TeamBuilder().withMaximumCapacity(3000).build()

        assertThat(equipo.stadiumCapacity).isEqualTo(3000)
    }
}