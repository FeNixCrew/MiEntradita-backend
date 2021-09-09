package ar.edu.unq.mientradita.model.builders

import ar.edu.unq.mientradita.model.Location
import ar.edu.unq.mientradita.model.Stadium

class StadiumBuilder {
    private var knownName: String = ""
    private var realName: String = ""
    private var capacity: Int = 0
    private var location: Location = LocationBuilder().build()

    fun build(): Stadium {
        return Stadium(knownName, realName, capacity, location)
    }

    fun withKnownName(knowName: String): StadiumBuilder {
        this.knownName = knowName
        return this
    }

    fun withRealName(realName: String): StadiumBuilder {
        this.realName = realName
        return this
    }

    fun withCapacity(capacity: Int): StadiumBuilder {
        this.capacity = capacity
        return this
    }

    fun withLocation(location: Location): StadiumBuilder {
        this.location = location
        return this
    }
}
