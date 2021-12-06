package ar.edu.unq.mientradita.model

import ar.edu.unq.mientradita.model.exception.BusinessException
import ar.edu.unq.mientradita.model.exception.PaymentNotRegistered
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Ticket(
    @ManyToOne(fetch=FetchType.EAGER)
    val match: Match,
    val reservation: LocalDateTime = LocalDateTime.now(),
    ) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var presentTime: LocalDateTime? = null
    @OneToOne(fetch= FetchType.EAGER, cascade = [CascadeType.ALL])
    val payment: Payment = Payment()

    fun markAsPresent(presentTime: LocalDateTime = LocalDateTime.now()): LocalDateTime {
        if(!isPaid()) throw PaymentNotRegistered()
        if (existPresentTime()) throw BusinessException("El espectador ya ha ingresado al partido")

        this.presentTime = presentTime
        return presentTime
    }

    fun savePaymentLink(paymentLink: String) {
        payment.saveLink(paymentLink)
    }

    fun markAsPaid(paymentId: String) {
        this.payment.savePayment(paymentId)
    }

    fun wasPresent(): Boolean{
        return existPresentTime() && match.isBeforeMatchEnd(presentTime!!)
    }

    fun isPendingAt(aTime: LocalDateTime): Boolean {
        return !existPresentTime() && match.isBeforeMatchEnd(aTime)
    }

    fun isFrom(aMatch: Match) = match.isEquals(aMatch)

    fun isPaid() = payment.isPaid()

    private fun existPresentTime() = presentTime != null
}
