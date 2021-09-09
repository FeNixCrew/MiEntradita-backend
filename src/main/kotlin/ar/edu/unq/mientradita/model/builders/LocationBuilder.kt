package ar.edu.unq.mientradita.model.builders

import ar.edu.unq.mientradita.model.Location

class LocationBuilder {
    private var location: String = ""
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    fun build(): Location {
        return Location(location, latitude, longitude)
    }

    fun withKnowName(location: String): LocationBuilder {
        this.location = location
        return this
    }

    fun withLatitude(latitude: Double): LocationBuilder {
        this.latitude = latitude
        return this
    }

    fun withLongitude(longitude: Double): LocationBuilder {
        this.longitude = longitude
        return this
    }
}
