package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.Team


interface TeamService {

    fun registerTeam(name:String,
                     stadiumKnownName:String,
                     stadiumRealName:String,
                     capacity: Int,
                     location: String,
                     stadiumLatitude: Double,
                     stadiumLongitude: Double
    ): Team

    fun findTeamBy(id: Long): Team


}