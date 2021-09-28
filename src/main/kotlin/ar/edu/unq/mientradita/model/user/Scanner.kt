package ar.edu.unq.mientradita.model.user

import javax.persistence.Entity
import javax.persistence.PrimaryKeyJoinColumn

@Entity
@PrimaryKeyJoinColumn(name="id")
class Scanner(username: String, password: String, email: String):
    User(username, password, email, Role.SCANNER)