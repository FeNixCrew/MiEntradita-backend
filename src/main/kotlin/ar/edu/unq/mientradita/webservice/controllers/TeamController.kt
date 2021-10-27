package ar.edu.unq.mientradita.webservice.controllers

import ar.edu.unq.mientradita.model.exception.MiEntraditaException
import ar.edu.unq.mientradita.service.CreateTeamRequest
import ar.edu.unq.mientradita.service.TeamService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.util.function.Consumer
import javax.validation.Valid

@RestController
@RequestMapping("/api/team")
class TeamController {

    @Autowired
    private lateinit var teamService: TeamService

    @RequestMapping(value = ["/details"], method = [RequestMethod.GET])
    fun getTeamDetails(@RequestParam teamName: String): ResponseEntity<*> {
        return ResponseEntity(teamService.getTeamDetails(teamName), HttpStatus.OK)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = ["/create"], method = [RequestMethod.POST])
    fun createTeam(@RequestBody @Valid createTeamRequest: CreateTeamRequest): ResponseEntity<*> {
        return ResponseEntity(teamService.registerTeam(createTeamRequest), HttpStatus.CREATED)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = ["/all"], method = [RequestMethod.GET])
    fun teams(): ResponseEntity<*> {
        return ResponseEntity(teamService.getTeams(), HttpStatus.OK)
    }
}