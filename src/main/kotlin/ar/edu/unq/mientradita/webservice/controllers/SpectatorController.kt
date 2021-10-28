package ar.edu.unq.mientradita.webservice.controllers

import ar.edu.unq.mientradita.model.exception.MiEntraditaException
import ar.edu.unq.mientradita.service.SpectatorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/spectator")
class SpectatorController {
    @Autowired
    private lateinit var spectatorService: SpectatorService

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = ["/tickets"], method = [RequestMethod.GET])
    fun pendingTicketsFrom(@RequestParam spectatorId: Long): ResponseEntity<*> {
        return ResponseEntity.ok(spectatorService.pendingTickets(spectatorId))
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = ["/new-reserve"], method = [RequestMethod.POST])
    fun reserveTicket(@RequestParam spectatorId: Long, matchId: Long): ResponseEntity<*> {
        return ResponseEntity(spectatorService.reserveTicket(spectatorId, matchId), HttpStatus.OK)
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = ["/favourite"], method = [RequestMethod.GET])
    fun favouriteTeamFor(@RequestParam spectatorId: Long): ResponseEntity<*> {
        return ResponseEntity(spectatorService.favouriteTeamFor(spectatorId), HttpStatus.OK)
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = ["/favourite"], method = [RequestMethod.POST])
    fun markAsFavourite(@RequestParam spectatorId: Long, teamId: Long): ResponseEntity<*> {
        return ResponseEntity(spectatorService.markAsFavourite(spectatorId, teamId), HttpStatus.CREATED)
    }
}