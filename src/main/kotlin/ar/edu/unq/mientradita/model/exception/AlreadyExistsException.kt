package ar.edu.unq.mientradita.model.exception

open class AlreadyExistsException(message: String) : MiEntraditaException(message)

class MatchAlreadyExistsException(home: String, away: String)
    : AlreadyExistsException("Ya se ha disputado un partido entre $home como local y $away como visitante")