package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.exception.MatchNotFoundException
import ar.edu.unq.mientradita.persistence.match.MatchRepository
import ar.edu.unq.mientradita.service.dto.Asistencia
import ar.edu.unq.mientradita.service.dto.AsistenciaDeEspectador
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AdminService {

    @Autowired
    private lateinit var matchRepository: MatchRepository

    @Transactional
    fun attendanceFor(matchId: Long, time: LocalDateTime = LocalDateTime.now()): List<AsistenciaDeEspectador> {
        val match = matchRepository.findById(matchId).orElseThrow { MatchNotFoundException() }
        val results = matchRepository.getSpectatorsAttendance(match)

        return results.map {
            AsistenciaDeEspectador(
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
    ): Asistencia {
        return when {
            presentTime != null -> {
                Asistencia.PRESENTE
            }
            matchStartTime.plusMinutes(90) < time -> {
                Asistencia.AUSENTE
            }
            else -> {
                Asistencia.PENDIENTE
            }
        }
    }
}