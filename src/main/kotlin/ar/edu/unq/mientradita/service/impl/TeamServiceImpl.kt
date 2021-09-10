package ar.edu.unq.mientradita.service.impl

import ar.edu.unq.mientradita.model.Location
import ar.edu.unq.mientradita.model.Stadium
import ar.edu.unq.mientradita.model.Team
import ar.edu.unq.mientradita.persistence.LocationRepository
import ar.edu.unq.mientradita.persistence.StadiumRepository
import ar.edu.unq.mientradita.persistence.TeamRepository
import ar.edu.unq.mientradita.service.TeamService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TeamServiceImpl: TeamService {
    @Autowired
    private lateinit var teamRepository: TeamRepository

    @Autowired
    private lateinit var stadiumRepository: StadiumRepository

    @Autowired
    private lateinit var locationRepository: LocationRepository

    override fun registerTeam(
        name: String,
        stadiumKnownName: String,
        stadiumRealName: String,
        capacity: Int,
        location: String,
        stadiumLatitude: Double,
        stadiumLongitude: Double
    ): Team {
        val stadiumLocation = locationRepository.save(Location(location, stadiumLatitude, stadiumLongitude))
        val stadium = stadiumRepository.save(Stadium(stadiumKnownName, stadiumRealName, capacity, stadiumLocation))

        return teamRepository.save(Team(name, stadium))
    }

    override fun findTeamBy(id: Long): Team {
        return teamRepository.findById(id).get()
    }

}