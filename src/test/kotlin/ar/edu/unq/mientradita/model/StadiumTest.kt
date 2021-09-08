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
    fun `a stadium have a know name`() {
        assertThat(monumental.knowName).isEqualTo("Monumental")
    }

    @Test
    fun `a stadium have a real name`() {
        assertThat(monumental.realName).isEqualTo("Antonio Vespucio Liberti")
    }

    @Test
    fun `a stadium has a certain capacity`() {
        assertThat(monumental.availableCapacity).isEqualTo(70074)
    }

    @Test
    fun `a stadium is located in a certain place`() {
        assertThat(monumental.location.location).isEqualTo("Av. Pres. Figueroa Alcorta 7597,CABA")
        assertThat(monumental.location.latitude).isEqualTo(-34.545278)
        assertThat(monumental.location.longuitude).isEqualTo(-58.449444)
    }
}