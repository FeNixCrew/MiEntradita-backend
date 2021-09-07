package ar.edu.unq.mientradita.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TeamTest {
    lateinit var racing: Team

    @BeforeEach
    fun setUp(){
        racing = Team("Racing")
    }

    @Test
    fun `two teams are NOT equals when have different name`(){
        val anotherRacing = Team("Racing club de Avellaneda")

        assertThat(racing.isEquals(anotherRacing)).isFalse
    }

    @Test
    fun `two teams are equals when have same name`(){
        val anotherRacing = Team("Racing")

        assertThat(racing.isEquals(anotherRacing)).isTrue
    }
}