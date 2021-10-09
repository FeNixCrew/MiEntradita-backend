package ar.edu.unq.mientradita.model.exception

class MatchAlreadyExists(home: String, away: String)
    : MiEntraditaException("Ya se ha disputado un partido entre $home como local y $away como visitante") {

}
