package ar.edu.unq.mientradita.webservice.controllers

import ar.edu.unq.mientradita.service.MailSenderService
import ar.edu.unq.mientradita.service.MatchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.validation.Valid
import javax.validation.constraints.*

@RestController
@RequestMapping("/api/match")
class MatchController {
    @Autowired
    private lateinit var matchService: MatchService

    @Autowired
    private lateinit var mailSenderService: MailSenderService

    @PreAuthorize("hasRole('SCANNER')")
    @RequestMapping(value = ["/comeIn"], method = [RequestMethod.POST])
    fun comeIn(@RequestBody comeInRequest: ComeInRequest): ResponseEntity<*> {
        return ResponseEntity.ok(matchService.comeIn(comeInRequest.spectatorId, comeInRequest.matchId))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = ["/create"], method = [RequestMethod.POST])
    fun createMatch(@RequestBody @Valid createMatchRequest: CreateMatchRequest): ResponseEntity<*> {
        val matchDTO = matchService.createMatch(createMatchRequest)
        mailSenderService.notifyToFansFrom(matchDTO)
        return ResponseEntity(matchDTO, HttpStatus.CREATED)
    }

    @RequestMapping(value = ["search"], method = [RequestMethod.GET])
    fun searchByPartialName(@RequestParam partialName: String, @RequestHeader("Authorization") token: String): ResponseEntity<*> {
        return ResponseEntity.ok(matchService.searchNextMatchsByPartialName(partialName, token))
    }

    @RequestMapping(value = ["/details"], method = [RequestMethod.GET])
    fun getMatchDetails(@RequestParam matchId: Long): ResponseEntity<*> {
        return ResponseEntity.ok(matchService.getMatchDetails(matchId))
    }

    @RequestMapping(value = ["/today-matchs"], method = [RequestMethod.GET])
    fun todayMatchs(): ResponseEntity<*> {
        return ResponseEntity(matchService.todayMatchs(), HttpStatus.OK)
    }

    @RequestMapping(value = ["/matchs"], method = [RequestMethod.GET])
    fun matchs(): ResponseEntity<*> {
        return ResponseEntity(matchService.matchs(), HttpStatus.OK)
    }

}

data class ComeInRequest(val spectatorId: Long, val matchId: Long)

data class CreateMatchRequest(
        @field:NotBlank(message = "El equipo local es requerido")
        val home: String,
        @field:NotBlank(message = "El equipo visitante es requerido")
        val away: String,
        val ticketPrice: Double,
        @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        val matchStartTime: LocalDateTime,
        val admittedPercentage: Int? = null
)