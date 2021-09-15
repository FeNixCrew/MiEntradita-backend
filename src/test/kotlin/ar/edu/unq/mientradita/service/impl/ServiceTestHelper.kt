package ar.edu.unq.mientradita.service.impl

import ar.edu.unq.mientradita.persistence.MatchRepository
import ar.edu.unq.mientradita.persistence.SpectatorRepository
import ar.edu.unq.mientradita.persistence.TicketRepository
import org.springframework.beans.factory.annotation.Autowired

class ServiceTestHelper {

    @Autowired
    private lateinit var ticketRepository: TicketRepository

    @Autowired
    private lateinit var  matchRepository: MatchRepository

    @Autowired
    private lateinit var spectatorRepository: SpectatorRepository

    fun clearDataSet() {
        ticketRepository.deleteAll()
        spectatorRepository.deleteAll()
        matchRepository.deleteAll()
    }

}