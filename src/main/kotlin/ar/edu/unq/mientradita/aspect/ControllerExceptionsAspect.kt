package ar.edu.unq.mientradita.aspect

import ar.edu.unq.mientradita.model.exception.MiEntraditaException
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Aspect
@Order(0)
@Component
class ControllerExceptionsAspect {

    @Around("execution(* ar.edu.unq.mientradita.webservice.controllers.*.*(..))")
    fun manageExceptions(proceedingJoinPoint: ProceedingJoinPoint): Any {
        return try {
            proceedingJoinPoint.proceed()
        } catch (exception: MiEntraditaException) {
            ResponseEntity.badRequest().body(exception.toMap())
        }
    }
}