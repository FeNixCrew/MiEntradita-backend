package ar.edu.unq.mientradita.model

import ar.edu.unq.mientradita.model.builders.StadiumBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StadiumTest {

    @Test
    fun `un estadio tiene un nombre`() {
        val estadio = StadiumBuilder().withName("Estadio Santiago Bernabeu").build()

        assertThat(estadio.name).isEqualTo("Estadio Santiago Bernabeu")
    }

    @Test
    fun `un estadio tiene como ubicacion una latitud y una longitud`() {
        val estadio = StadiumBuilder().withLatitude(40.45306).withLongitude(-3.68835).build()

        assertThat(estadio.latitude).isEqualTo(40.45306)
        assertThat(estadio.longitude).isEqualTo(-3.68835)
    }

    @Test
    fun `un estadio tiene una capacidad maxima`(){
        val estadio = StadiumBuilder().withCapacity(20).build()

        assertThat(estadio.capacity).isEqualTo(20)
    }


}