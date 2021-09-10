package ar.edu.unq.mientradita.persistence

import ar.edu.unq.mientradita.model.Stadium
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface StadiumRepository : CrudRepository<Stadium, Long>
