package ar.edu.unq.mientradita.model

import ar.edu.unq.mientradita.model.exception.AlreadyPresentInGameException
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Ticket(
    @ManyToOne(fetch=FetchType.LAZY)
    val match: Match,
    val reservation: LocalDateTime = LocalDateTime.now(),
    val price: Double
    ) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var presentTime: LocalDateTime? = null

    fun markAsPresent(presentTime: LocalDateTime = LocalDateTime.now()): LocalDateTime {
        if (existPresentTime()) throw AlreadyPresentInGameException()
        this.presentTime = presentTime
        return presentTime
    }

    fun wasPresent(): Boolean{
        return existPresentTime() && match.isBeforeMatchEnd(presentTime!!)
    }

    fun isPendingAt(aTime: LocalDateTime): Boolean {
        return !existPresentTime() && match.isBeforeMatchEnd(aTime)
    }

    private fun existPresentTime() = presentTime != null
}

