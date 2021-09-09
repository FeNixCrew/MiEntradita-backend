package ar.edu.unq.mientradita.model

import ar.edu.unq.mientradita.model.builders.TeamBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TeamTest {
    lateinit var racing: Team

    @BeforeEach
    fun setUp(){
        racing = TeamBuilder().withName("Racing").build()
    }

    @Test
    fun `dos equipos no son iguales cuando tienen nombres distintos`(){
        val otroRacing = TeamBuilder().withName("Racing club de Avellaneda").build()

        assertThat(racing.isEquals(otroRacing)).isFalse
    }

    @Test
    fun `dos equipos son iguales cuando tienen el mismo nombre`(){
        val elMismoRacing = TeamBuilder().withName("Racing").build()

        assertThat(racing.isEquals(elMismoRacing)).isTrue
    }

    @Test
    fun `dos equipos son iguales cuando tienen el mismo nombre sin importar el case sensitive`(){
        val elMismoRacing = TeamBuilder().withName("racing").build()

        assertThat(racing.isEquals(elMismoRacing)).isTrue
    }
}