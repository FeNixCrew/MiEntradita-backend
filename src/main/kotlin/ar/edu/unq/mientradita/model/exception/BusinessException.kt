package ar.edu.unq.mientradita.model.exception

import java.time.LocalDateTime

open class BusinessException(message: String) : MiEntraditaException(message)

class TeamNearlyPlayException(teamName:String, expectedDate: LocalDateTime, registeredMatchDate: LocalDateTime)
    : BusinessException("$teamName no puede jugar el " + format(expectedDate) +
        " porque tiene un partido el dia " + format(registeredMatchDate))


fun format(time: LocalDateTime)= "${time.dayOfMonth}/${time.monthValue}/${time.year}"
