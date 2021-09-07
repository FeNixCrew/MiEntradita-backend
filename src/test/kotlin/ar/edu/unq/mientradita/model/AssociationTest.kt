package ar.edu.unq.mientradita.model

import ar.edu.unq.mientradita.model.exception.DuplicatedGameException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AssociationTest {
    lateinit var association: Association
    lateinit var racing: Team
    lateinit var river: Team

    @BeforeEach
    fun setUp(){
        association = Association()
        racing = Team("Racing")
        river = Team("River")
    }

    @Test
    fun `an association can create a game`(){
        val game = association.createGame(racing, river)

        assertThat(association.games.contains(game)).isTrue
    }

    @Test
    fun `an association cannot create an existing game`() {
        association.createGame(racing, river)

        val exception = assertThrows<DuplicatedGameException> {
            association.createGame(racing, river)
        }

        assertThat(exception.message).isEqualTo("El partido ya fue creado con Racing de local y River de visitante")
    }
}