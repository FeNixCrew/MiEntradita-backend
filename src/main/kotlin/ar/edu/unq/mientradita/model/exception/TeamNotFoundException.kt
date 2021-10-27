package ar.edu.unq.mientradita.model.exception

class TeamNotFoundException(teamName: String): MiEntraditaException("Equipo llamado $teamName no encontrado")

class TeamNotRegisteredException(): MiEntraditaException("Equipo no registrado")