package ar.edu.unq.mientradita.model.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserTest {

    @Test
    fun `un espectador tiene rol de usuario`() {
        val espectador = Spectator("", "", "", "", "99999", "")

        assertThat(espectador.role).isEqualTo(Role.ROLE_USER)
    }

    @Test
    fun `un escaneador tiene rol de scanner`() {
        val espectador = Scanner("", "", "")

        assertThat(espectador.role).isEqualTo(Role.ROLE_SCANNER)
    }

    @Test
    fun `un administrador tiene rol de admin`() {
        val espectador = Admin("", "", "")

        assertThat(espectador.role).isEqualTo(Role.ROLE_ADMIN)
    }
}