package ar.edu.unq.mientradita.webservice

import ar.edu.unq.mientradita.model.exception.MiEntraditaException
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
    fun logIn(@RequestBody loginRequest: LoginRequest): ResponseEntity<*> {
        return try {
            ResponseEntity.ok(spectatorService.login(loginRequest))
        } catch (exception: MiEntraditaException){
            ResponseEntity.badRequest().body(exception.toMap())
        }
    }

    @RequestMapping(value=["/tickets"], method = [RequestMethod.GET])
    fun pendingTicketsFrom(@RequestParam spectatorId: Long): ResponseEntity<*> {
        return try {
            ResponseEntity.ok(spectatorService.pendingTickets(spectatorId))
        } catch (exception: MiEntraditaException){
            ResponseEntity.badRequest().body(exception.toMap())
        }
    }
}


data class LoginRequest(val username: String, val password: String)