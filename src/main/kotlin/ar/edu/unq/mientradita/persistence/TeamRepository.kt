package ar.edu.unq.mientradita.persistence

import ar.edu.unq.mientradita.model.Team
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TeamRepository: CrudRepository<Team, Long> {

}
