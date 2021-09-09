package ar.edu.unq.mientradita.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StadiumTest {

    lateinit var monumental: Stadium

    @BeforeEach
    fun setUp() {
        val location = Location("Av. Pres. Figueroa Alcorta 7597,CABA", -34.545278, -58.449444)
        monumental = Stadium("Monumental",
                "Antonio Vespucio Liberti",
                70074,
                location)
    }

    @Test
    fun `un estadio sabe cual es su nombre conocido`() {
        assertThat(monumental.knownName).isEqualTo("Monumental")
    }

    @Test
    fun `un estadio conoce su nombre real`() {
        assertThat(monumental.realName).isEqualTo("Antonio Vespucio Liberti")
    }

    @Test
    fun `un estadio conoce su capacidad`() {
        assertThat(monumental.capacity).isEqualTo(70074)
    }

    @Test
    fun `un estadio conoce su ubicacion`() {
        assertThat(monumental.location.location).isEqualTo("Av. Pres. Figueroa Alcorta 7597,CABA")
        assertThat(monumental.location.latitude).isEqualTo(-34.545278)
        assertThat(monumental.location.longitude).isEqualTo(-58.449444)
    }
}