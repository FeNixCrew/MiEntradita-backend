package ar.edu.unq.mientradita.persistence

import ar.edu.unq.mientradita.model.Stadium
import org.springframework.data.repository.CrudRepository

interface StadiumRepository: CrudRepository<Stadium, Long>