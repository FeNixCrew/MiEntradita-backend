package ar.edu.unq.mientradita.service

//@Deprecated("Sobrediseniado")
//class TeamService {
//    @Autowired
//    private lateinit var teamRepository: TeamRepository
//
//    @Autowired
//    private lateinit var stadiumRepository: StadiumRepository
//
//    @Autowired
//    private lateinit var locationRepository: LocationRepository
//
//    @Transactional
//    fun registerTeam(
//        name: String,
//        stadiumKnownName: String,
//        stadiumRealName: String,
//        capacity: Int,
//        location: String,
//        stadiumLatitude: Double,
//        stadiumLongitude: Double
//    ): Team {
//        val stadiumLocation = locationRepository.save(Location(location, stadiumLatitude, stadiumLongitude))
//        val stadium = stadiumRepository.save(Stadium(stadiumKnownName, stadiumRealName, capacity, stadiumLocation))
//
//        return teamRepository.save(Team(name, stadium))
//    }
//
//    @Transactional
//    fun findTeamBy(id: Long): Team {
//        return teamRepository.findById(id).get()
//    }

//}