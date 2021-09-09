package ar.edu.unq.mientradita.model

import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Ticket(
    @ManyToOne(fetch=FetchType.LAZY)
    val match: Match,
    val reservation: LocalDateTime = LocalDateTime.now(),
    var state: Attend = Attend.PENDING,
    ) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun markAsPresent() {
        state = Attend.PRESENT
    }

    fun markAsAbsent() {
        state = Attend.ABSENT
    }
}