package ar.edu.unq.mientradita.model

import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Ticket(
    @ManyToOne(fetch=FetchType.LAZY)
    val match: Match,
    val reservation: LocalDateTime = LocalDateTime.now(),
    ) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var presentTime: LocalDateTime? = null

    fun markAsPresent(presentTime: LocalDateTime = LocalDateTime.now()): LocalDateTime {
        this.presentTime = presentTime
        return presentTime
    }

    fun wasPresent(): Boolean{
        return presentTime != null && match.isBeforeMatchEnd(presentTime!!)
    }
}

