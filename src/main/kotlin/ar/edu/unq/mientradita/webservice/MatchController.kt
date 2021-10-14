package ar.edu.unq.mientradita.webservice

import ar.edu.unq.mientradita.model.Match
import ar.edu.unq.mientradita.model.exception.MiEntraditaException
import ar.edu.unq.mientradita.service.MatchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.function.Consumer
import javax.validation.Valid
import javax.validation.constraints.*

@RestController
@RequestMapping("/api/match")
class MatchController {
    @Autowired
    private lateinit var matchService: MatchService

    @PreAuthorize("hasRole('SCANNER')")
    @RequestMapping(value=["/comeIn"], method = [RequestMethod.POST])
    fun comeIn(@RequestBody comeInRequest: ComeInRequest): ResponseEntity<*> {
        return try {
            ResponseEntity.ok(matchService.comeIn(comeInRequest.spectatorId, comeInRequest.matchId))
        } catch (exception: MiEntraditaException){
            ResponseEntity.badRequest().body(exception.toMap())
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = ["/create"], method = [RequestMethod.POST])
    fun createMatch(@RequestBody @Valid createMatchRequest: CreateMatchRequest): ResponseEntity<*>{
        return try{
            ResponseEntity(matchService.createMatch(createMatchRequest), HttpStatus.CREATED)
        } catch (exception: MiEntraditaException) {
            ResponseEntity.badRequest().body(exception.toMap())
        }
    }

    @RequestMapping(value = ["search"], method = [RequestMethod.GET])
    fun searchByPartialName(@RequestParam partialName: String): ResponseEntity<*> {
        return try {
            ResponseEntity.ok(matchService.searchNextMatchsByPartialName(partialName))
        } catch (exception: MiEntraditaException) {
            ResponseEntity.badRequest().body(exception.toMap())
        }
    }

    @RequestMapping(value = ["/details"], method = [RequestMethod.GET])
    fun getMatchDetails(@RequestParam matchId: Long): ResponseEntity<*> {
        return try {
            ResponseEntity.ok(matchService.getMatchDetails(matchId))
        } catch (exception: MiEntraditaException) {
            ResponseEntity.badRequest().body(exception.toMap())
        }
    }

    @RequestMapping(value = ["/today-matchs"], method = [RequestMethod.GET])
    fun todayMatchs(): ResponseEntity<*>{
        return try{
            ResponseEntity(matchService.todayMatchs(), HttpStatus.OK)
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

data class ComeInRequest(val spectatorId: Long, val matchId: Long)

data class CreateMatchRequest(
        @field:NotBlank(message = "El equipo local es requerido")
        val home: String,
        @field:NotBlank(message = "El equipo visitante es requerido")
        val away: String,
        val ticketPrice: Double,
        @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        val matchStartTime: LocalDateTime,
        @field:NotBlank(message = "El estadio local es requerido")
        val stadium: String
)