package ar.edu.unq.mientradita.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Payment(var link: String? = null) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    private var paymentId : String? = null

    fun saveLink(paymentLink: String) {
        this.link = paymentLink
    }

    fun savePayment(paymentId: String) {
        this.paymentId = paymentId
    }

    fun isPayed() = paymentId != null
}