package ar.edu.unq.mientradita.webservice

import ar.edu.unq.mientradita.service.SpectatorDTO
import ar.edu.unq.mientradita.service.SpectatorService
import ar.edu.unq.mientradita.service.TicketDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/spectator")
class SpectatorController {
    @Autowired
    private lateinit var spectatorService: SpectatorService

    @RequestMapping(value=["/login"], method = [RequestMethod.POST])
    fun logIn(@RequestBody loginRequest: LoginRequest): ResponseEntity<SpectatorDTO> {
        return ResponseEntity.ok(spectatorService.login(loginRequest))
    }

    @RequestMapping(value=["/tickets"], method = [RequestMethod.GET])
    fun pendingTicketsFrom(@RequestParam spectatorId: Long): ResponseEntity<List<TicketDTO>> {
        return ResponseEntity.ok(spectatorService.pendingTickets(spectatorId))
    }
}


data class LoginRequest(val username: String, val password: String)