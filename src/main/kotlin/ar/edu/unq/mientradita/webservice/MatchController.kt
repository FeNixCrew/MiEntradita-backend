package ar.edu.unq.mientradita.webservice

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.exception.MiEntraditaException
import ar.edu.unq.mientradita.service.MatchService
import ar.edu.unq.mientradita.service.SpectatorService
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/match")
class MatchController {
    @Autowired
    private lateinit var matchService: MatchService

    @RequestMapping(value=["/comeIn"], method = [RequestMethod.POST])
    fun comeIn(@RequestBody comeInRequest: ComeInRequest): ResponseEntity<*> {
        return try {
            ResponseEntity.ok(matchService.comeIn(comeInRequest.spectatorId, comeInRequest.matchId))
        } catch (exception: MiEntraditaException){
            ResponseEntity.badRequest().body(exception.toMap())
        }
    }

    @RequestMapping(value = ["/create"], method = [RequestMethod.POST])
    fun createMatch(@RequestBody createMatchRequest: CreateMatchRequest): ResponseEntity<*>{
        return ResponseEntity.ok(matchService.createMatch(createMatchRequest))
    }

}

data class ComeInRequest(val spectatorId: Long, val matchId: Long)

data class CreateMatchRequest(
        val home: String,
        val away: String,
        val ticketPrice: Double,
        val matchStartTime: LocalDateTime
) {
    fun toModel() = Match(home, away, matchStartTime)
}