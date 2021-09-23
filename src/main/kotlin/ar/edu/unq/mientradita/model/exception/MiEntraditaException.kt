package ar.edu.unq.mientradita.model.exception

abstract class MiEntraditaException(msg: String): RuntimeException(msg) {

    fun toMap(): Map<String, String> {
        return mapOf(
            Pair("exception", this.javaClass.simpleName),
            Pair("message", this.message!!)
        )
    }
}