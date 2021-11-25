package ar.edu.unq.mientradita.model.exception

import ar.edu.unq.mientradita.model.builders.MatchBuilder
import ar.edu.unq.mientradita.model.builders.TicketBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class MiEntraditaExceptionTest {

    @Test
    fun `las excepciones personalizadas del sistema saben expresarse como un diccionario`(){
        val horarioDePartido = LocalDateTime.of(2021, 9, 12, 16, 0, 0)
        val partido = MatchBuilder().withMatchStart(horarioDePartido).build()
        val entrada = TicketBuilder().withGame(partido).build()
        val horaDeEntrada = horarioDePartido.minusHours(1)
        entrada.markAsPresent(horaDeEntrada)

        val exception = assertThrows<BusinessException> { entrada.markAsPresent(horaDeEntrada) }

        val diccionarioEsperado = mapOf(
            Pair("exception", exception.javaClass.simpleName),
            Pair("message", exception.message)
        )

        assertThat(exception.toMap()).isEqualTo(diccionarioEsperado)
    }
}