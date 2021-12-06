package ar.edu.unq.mientradita.service.client

import ar.edu.unq.mientradita.model.Ticket
import ar.edu.unq.mientradita.model.exception.BusinessException
import ar.edu.unq.mientradita.model.user.Spectator
import com.mercadopago.MercadoPago
import com.mercadopago.resources.Payment
import com.mercadopago.resources.Preference
import com.mercadopago.resources.datastructures.preference.BackUrls
import com.mercadopago.resources.datastructures.preference.Item
import com.mercadopago.resources.datastructures.preference.Payer
import com.mercadopago.resources.datastructures.preference.PaymentMethods
import org.springframework.stereotype.Service

@Service
class MercadoPagoClient {
    fun createLink(spectator: Spectator, ticket: Ticket): String {
        MercadoPago.SDK.setAccessToken(System.getenv()["MP_ACCESS_TOKEN"])
        val preference = Preference()

        val payer = Payer()
        payer.email = spectator.email
        payer.name = spectator.name
        payer.surname = spectator.surname
        preference.payer = payer

        val backUrls = BackUrls()
        backUrls.failure = "http://localhost:3000/${spectator.username}/payments/pending"
        backUrls.pending = "http://localhost:3000/${spectator.username}/payments/pending"
        backUrls.success = "http://localhost:3000/${spectator.username}/payments/success/${ticket.id}"
        preference.backUrls = backUrls

        val paymentMethods = PaymentMethods()
        paymentMethods.setExcludedPaymentTypes("ticket", "atm")
        preference.paymentMethods = paymentMethods

        preference.autoReturn = Preference.AutoReturn.all

        val item = Item()
        item.title = "Entrada - ${ticket.match.home.name} vs ${ticket.match.away.name}"
        item.quantity = 1
        item.unitPrice = ticket.match.ticketPrice
        preference.appendItem(item)

        return preference.save().sandboxInitPoint
    }

    fun validatePaymentId(paymentId: String) {
        val payment: Payment = Payment.findById(paymentId)

        if (payment.dateCreated == null) throw BusinessException("El id de pago no es valido")

        if (payment.status != Payment.Status.approved) throw BusinessException("El pago de la entrada no posee un estado de aprobado")
    }
}

/*
resultado   tipo tarjeta	    nro	                    cod. seguridad	        vencimiento
Success     Mastercard	        5031 7557 3453 0604	    123	                    11/25
Failure     Mastercard	        5182 4320 0439 0716	    347	                    04/23
 */