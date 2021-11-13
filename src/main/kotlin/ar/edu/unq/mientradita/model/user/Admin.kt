package ar.edu.unq.mientradita.model.user

import javax.persistence.Entity
import javax.persistence.PrimaryKeyJoinColumn

@Entity
@PrimaryKeyJoinColumn(name="id")
class Admin(username: String, password: String, email: String):
    MiEntraditaUser(username, password, email, Role.ROLE_ADMIN)