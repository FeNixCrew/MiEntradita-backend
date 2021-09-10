package ar.edu.unq.mientradita.service.impl

import ar.edu.unq.mientradita.model.Spectator
import ar.edu.unq.mientradita.persistence.SpectatorRepository
import ar.edu.unq.mientradita.persistence.TicketRepository
import ar.edu.unq.mientradita.service.SpectatorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SpectatorServiceImpl: SpectatorService {

    @Autowired
    private lateinit var spectatorRepository: SpectatorRepository


    override fun createSpectator(name: String, surname: String, username: String, password: String, email: String, dni: Int): Spectator {
        val spectator = Spectator(surname,username,name,email,dni,password)
        return spectatorRepository.save(spectator)
    }

    override fun findSpectatorById(id: Long): Spectator {
        return spectatorRepository.findById(id).get()
    }
}