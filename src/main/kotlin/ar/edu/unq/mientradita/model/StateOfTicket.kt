package ar.edu.unq.mientradita.model

enum class StateOfTicket {
    PENDING { override fun wasAttended() = false },
    PRESENT { override fun wasAttended() = true },
    ABSENT { override fun wasAttended() = true };

    abstract fun wasAttended(): Boolean
}