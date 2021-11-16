package ar.edu.unq.mientradita.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Payment(var paymentLink: String? = null) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun savePaymentLink(paymentLink: String) {
        this.paymentLink = paymentLink
    }
}