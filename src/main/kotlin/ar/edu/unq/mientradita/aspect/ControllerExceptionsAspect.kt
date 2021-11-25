package ar.edu.unq.mientradita.aspect

import ar.edu.unq.mientradita.model.exception.AlreadyExistsException
import ar.edu.unq.mientradita.model.exception.BusinessException
import ar.edu.unq.mientradita.model.exception.MiEntraditaException
import ar.edu.unq.mientradita.model.exception.NotFoundException
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
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
        } catch (exception: BusinessException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.toMap())
        } catch (exception: AlreadyExistsException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(exception.toMap())
        } catch (exception: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.toMap())
        } catch (exception: RuntimeException) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.message)
        }
    }
}
