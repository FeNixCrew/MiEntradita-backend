package ar.edu.unq.mientradita.model.exception

import java.lang.RuntimeException

class TicketsOffException: RuntimeException("Ya no hay mas entradas") {
}