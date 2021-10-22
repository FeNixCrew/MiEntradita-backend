package ar.edu.unq.mientradita.webservice

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
        return try{
            ResponseEntity(teamService.getTeamDetails(teamName), HttpStatus.OK)
        } catch (exception: MiEntraditaException) {
            ResponseEntity.badRequest().body(exception.toMap())
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = ["/create"], method = [RequestMethod.POST])
    fun createTeam(@RequestBody @Valid createTeamRequest: CreateTeamRequest): ResponseEntity<*> {
        return try{
            ResponseEntity(teamService.registerTeam(createTeamRequest), HttpStatus.CREATED)
        } catch (exception: MiEntraditaException) {
            ResponseEntity.badRequest().body(exception.toMap())
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = ["/all"], method = [RequestMethod.GET])
    fun teams(): ResponseEntity<*>{
        return try{
            ResponseEntity(teamService.getTeams(), HttpStatus.OK)
        } catch (exception: MiEntraditaException) {
            ResponseEntity.badRequest().body(exception.toMap())
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): Map<String, String?>? {
        val errors: MutableMap<String, String?> = HashMap()
        ex.bindingResult.fieldErrors.forEach(Consumer { error: FieldError -> errors[error.field] = error.defaultMessage })
        return errors
    }
}