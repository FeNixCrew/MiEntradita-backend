package ar.edu.unq.mientradita.webservice.controllers

import ar.edu.unq.mientradita.service.AdminService
import ar.edu.unq.mientradita.webservice.controllers.ComeInRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
class AdminController {

    @Autowired
    private lateinit var adminService: AdminService

    @RequestMapping(value = ["/match-attendance"], method = [RequestMethod.GET])
    fun comeIn(@RequestParam matchId: Long): ResponseEntity<*> {
        return ResponseEntity.ok(adminService.getMatchInformation(matchId))
    }

}