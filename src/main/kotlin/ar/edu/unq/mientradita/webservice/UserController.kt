package ar.edu.unq.mientradita.webservice

import ar.edu.unq.mientradita.service.SpectatorDTO
import ar.edu.unq.mientradita.service.SpectatorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class UserController {
    @Autowired
    private lateinit var spectatorService: SpectatorService

    @RequestMapping(value=["/login"], method = [RequestMethod.POST])
    fun logIn(@RequestBody loginRequest: LoginRequest): ResponseEntity<SpectatorDTO> {
        return ResponseEntity.ok(spectatorService.login(loginRequest))
    }
}


data class LoginRequest(val username: String, val password: String)