package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.exception.MatchDoNotExistsException
import ar.edu.unq.mientradita.persistence.match.MatchRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AdminService {

    @Autowired
    private lateinit var matchRepository: MatchRepository

    @Transactional
    fun getMatchInformation(matchId: Long, time: LocalDateTime = LocalDateTime.now()): List<SpectatorAttendance> {
        val match = matchRepository.findById(matchId).orElseThrow { MatchDoNotExistsException() }
        val results = matchRepository.getSpectatorsAttendance(match)

        return results.map {
            SpectatorAttendance(
                it.id,
                it.dni,
                it.name + " " + it.surname,
                getAttendance(match.matchStartTime, it.presentTime, time)
            )
        }
    }

    private fun getAttendance(
        matchStartTime: LocalDateTime,
        presentTime: LocalDateTime?,
        time: LocalDateTime
    ): Attendance {
        return when {
            presentTime != null -> {
                Attendance.PRESENT
            }
            matchStartTime.plusMinutes(90) < time -> {
                Attendance.ABSENT
            }
            else -> {
                Attendance.PENDING
            }
        }
    }
}

data class SpectatorAttendance(
    val id: Long,
    val dni: Int,
    val fullname: String,
    val attendance: Attendance,
)

enum class Attendance { PRESENT, ABSENT, PENDING; }