package ar.edu.unq.mientradita.webservice.config.security

import ar.edu.unq.mientradita.model.user.Role
import ar.edu.unq.mientradita.model.user.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component
import java.util.*

@Component
class JWTUtil {
    @Value("\${jwt.secret}")
    private val secret: String? = null

    fun generateToken(user: User): String {
        val token = Jwts
            .builder()
            .setSubject("MiEntradita JWTToken")
            .claim("username", user.username)
            .claim("authorities", listOf(user.role.toString()))
            .setIssuedAt(Date(System.currentTimeMillis()))
            .signWith(
                SignatureAlgorithm.HS512,
                this.secret
            ).compact()

        return "Bearer $token"
    }

    fun obtainClaims(bearerToken: String): Claims {
        val token = bearerToken.replace("Bearer ", "")

        return Jwts
            .parser()
            .setSigningKey(this.secret)
            .parseClaimsJws(token).body
    }

    fun getUsername(bearerToken: String): String {
        return  obtainClaims(bearerToken)["username"].toString()
    }

    fun isRoleUser(token: String) = getRole(token) == Role.ROLE_USER

    fun getRole(bearerToken: String): Role {
        val authority = obtainClaims(bearerToken)["authorities"] as List<String>

        return Role.fromString(authority[0])
    }

    fun isValidToken(bearerToken: String): Boolean {
        val claims = obtainClaims(bearerToken)
        return bearerToken.startsWith("Bearer ") && claims["authorities"] != null && claims["username"] != null
    }
}