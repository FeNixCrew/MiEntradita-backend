package ar.edu.unq.mientradita.model

import ar.edu.unq.mientradita.model.builders.StadiumBuilder
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
        val estadio = StadiumBuilder().withName("El Monumental").build()
        val equipo = TeamBuilder().withStadium(estadio).build()

        assertThat(equipo.stadium).isEqualTo(estadio)
    }

}