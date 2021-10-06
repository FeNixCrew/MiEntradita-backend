package ar.edu.unq.mientradita.model.exception

import java.time.LocalDateTime

class TeamNearlyPlayException(teamName:String, expectedDate: LocalDateTime, registeredMatchDate: LocalDateTime )
    : MiEntraditaException("$teamName no puede jugar el " + format(expectedDate) +
        " porque tiene un partido el dia " + format(registeredMatchDate))


fun format(time: LocalDateTime): String {
    return "${time.dayOfMonth}/${time.monthValue}/${time.year} a las " +
            "${time.hour}:${time.minute}hs"
}