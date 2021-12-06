package ar.edu.unq.mientradita.model.builders
import ar.edu.unq.mientradita.model.Stadium

class StadiumBuilder {

    private var name: String = "a"
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var capacity: Int = 0

    fun build(): Stadium {
        return Stadium(name, latitude, longitude, capacity)
    }

    fun withName(name: String): StadiumBuilder {
        this.name = name
        return this
    }

    fun withLatitude(latitude: Double): StadiumBuilder {
        this.latitude = latitude
        return this
    }

    fun withLongitude(longitude: Double): StadiumBuilder {
        this.longitude = longitude
        return this
    }

    fun withCapacity(capacity: Int): StadiumBuilder {
        this.capacity = capacity
        return this
    }
}