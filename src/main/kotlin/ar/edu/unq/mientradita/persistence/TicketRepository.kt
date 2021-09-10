package ar.edu.unq.mientradita.persistence

import ar.edu.unq.mientradita.model.Ticket
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository: CrudRepository<Ticket,Long>