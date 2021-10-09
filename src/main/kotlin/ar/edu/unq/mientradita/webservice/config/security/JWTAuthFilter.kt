package ar.edu.unq.mientradita.webservice.config.security

import ar.edu.unq.mientradita.model.user.User
import ar.edu.unq.mientradita.persistence.UserRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.security.core.userdetails.User as SpringUserDetails

@Component
class JWTAuthFilter : OncePerRequestFilter() {

    @Autowired
    private lateinit var jwtUtil: JWTUtil

    @Autowired
    private lateinit var jwtUserDetailsService: JwtUserDetailsService

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        validateToken(request)

        try {
            chain.doFilter(request, response)
        } catch (e: ExpiredJwtException) {
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.message)
            return
        } catch (e: UnsupportedJwtException) {
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.message)
            return
        } catch (e: MalformedJwtException) {
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.message)
            return
        }
    }

    private fun validateToken(request: HttpServletRequest) {
        var bearerToken: String? = null
        if (existJWTToken(request)) bearerToken = request.getHeader("Authorization")


        if (bearerToken != null && jwtUtil.isValidToken(bearerToken)) {
            setUpSpringAuthentication(jwtUtil.obtainClaims(bearerToken))
        } else {
            SecurityContextHolder.clearContext()
        }
    }

    private fun existJWTToken(request: HttpServletRequest): Boolean {
        val authenticationHeader = request.getHeader("Authorization")
        return authenticationHeader != null && authenticationHeader.startsWith("Bearer ")
    }

    private fun setUpSpringAuthentication(claims: Claims) {
        val userDetails = jwtUserDetailsService.loadUserByUsername(claims["username"].toString())
        val auth = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        SecurityContextHolder.getContext().authentication = auth
    }
}

@Service
class JwtUserDetailsService : UserDetailsService {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Transactional
    override fun loadUserByUsername(username: String): UserDetails {
        val systemUser: User = userRepository.findByUsername(username).orElseThrow { UsernameNotFoundException("User not found with username: $username")  }

        return SpringUserDetails(
            systemUser.username, systemUser.password,
            AuthorityUtils.commaSeparatedStringToAuthorityList(systemUser.role.toString())
        )
    }
}