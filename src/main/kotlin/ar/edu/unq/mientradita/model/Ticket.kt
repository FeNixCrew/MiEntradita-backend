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
    var attendTime: LocalDateTime? = null

    fun markAsPresent(attend: LocalDateTime = LocalDateTime.now()): LocalDateTime {
        attendTime = attend
        return attend
    }

    fun isAttend(): Boolean{
        return if(attendTime == null){
            false
        }
        else !(match.matchStartTime < LocalDateTime.now() && attendTime == null)
    }
}