package ar.edu.unq.mientradita.webservice.controllers

import ar.edu.unq.mientradita.service.MatchService
import ar.edu.unq.mientradita.service.dto.ComeInRequest
import ar.edu.unq.mientradita.service.dto.CreateMatchRequest
import ar.edu.unq.mientradita.webservice.config.CHANNEL
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/match")
class MatchController {
    @Autowired
    private lateinit var matchService: MatchService

    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, String>

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @PreAuthorize("hasRole('SCANNER')")
    @RequestMapping(value = ["/comeIn"], method = [RequestMethod.POST])
    fun comeIn(@RequestBody comeInRequest: ComeInRequest): ResponseEntity<*> {
        return ResponseEntity.ok(matchService.comeIn(comeInRequest.spectatorId, comeInRequest.matchId))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = ["/create"], method = [RequestMethod.POST])
    fun createMatch(@RequestBody @Valid createMatchRequest: CreateMatchRequest): ResponseEntity<*> {
        val matchDTO = matchService.createMatch(createMatchRequest)

        redisTemplate.convertAndSend(CHANNEL, objectMapper.writeValueAsString(matchDTO))

        return ResponseEntity(matchDTO, HttpStatus.CREATED)
    }

    @PreAuthorize("hasRole('ADMIN') || hasRole('USER')")
    @RequestMapping(value = ["search"], method = [RequestMethod.GET])
    fun searchByPartialName(
        @RequestParam partialName: String,
        @RequestParam isFinished: Boolean? = null,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<*> {
        return ResponseEntity.ok(matchService.searchNextMatchsByPartialName(partialName, token, isFinished))
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

    @RequestMapping(value = ["/next-matches"], method = [RequestMethod.GET])
    fun nextMatchesOfFavoriteTeam(): ResponseEntity<*> {
        return ResponseEntity(matchService.nextMatches(), HttpStatus.OK)
    }

}
