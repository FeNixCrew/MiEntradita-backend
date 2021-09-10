package ar.edu.unq.mientradita.persistence

import ar.edu.unq.mientradita.model.Location
import org.springframework.data.repository.CrudRepository

interface LocationRepository: CrudRepository<Location, Long>
