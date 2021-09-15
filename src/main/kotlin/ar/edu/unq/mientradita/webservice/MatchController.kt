package ar.edu.unq.mientradita.webservice

import ar.edu.unq.mientradita.service.MatchService
import ar.edu.unq.mientradita.service.SpectatorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class MatchController {
    @Autowired
    private lateinit var matchService: MatchService

    fun comeIn(@RequestBody comeInRequest: ComeInRequest): ResponseEntity<String> {
        return ResponseEntity.ok(matchService.comeIn(comeInRequest.spectatorId, comeInRequest.matchId))
    }
}

data class ComeInRequest(val spectatorId: Long, val matchId: Long)