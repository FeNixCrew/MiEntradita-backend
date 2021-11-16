package ar.edu.unq.mientradita.service.client

import ar.edu.unq.mientradita.model.Ticket
import ar.edu.unq.mientradita.model.user.Spectator
import com.mercadopago.MercadoPago
import com.mercadopago.resources.Preference
import com.mercadopago.resources.datastructures.preference.BackUrls
import com.mercadopago.resources.datastructures.preference.Item
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MercadoPagoClient {
    fun createLink(spectator: Spectator, ticket: Ticket): String {
        MercadoPago.SDK.setAccessToken(System.getenv()["MP_ACCESS_TOKEN"])
        val preference = Preference()

        val backUrls = BackUrls()
        backUrls.failure = "http://localhost:3000/${spectator.username}/checkout/error/${ticket.id}"
        backUrls.pending = "http://localhost:3000/${spectator.username}/checkout/${ticket.id}"
        backUrls.success = "http://localhost:3000/checkout/success/${ticket.id}"
        preference.backUrls = backUrls

        val item = Item()
        item.title = "Entrada - ${ticket.match.home.name} vs ${ticket.match.away.name}"
        item.quantity = 1
        item.unitPrice = ticket.match.ticketPrice
        preference.appendItem(item)

        val paymentLink: String = preference.save().sandboxInitPoint
        ticket.savePaymentLink(paymentLink)
        return paymentLink
    }
}

/*
resultado   tipo tarjeta	    nro	                    cod. seguridad	        vencimiento
Success     Mastercard	        5031 7557 3453 0604	    123	                    11/25
Failure     Mastercard	        5182 4320 0439 0716	    347	                    04/23
Success     American Express	3711 803032 57522	    1234	                11/25
 */