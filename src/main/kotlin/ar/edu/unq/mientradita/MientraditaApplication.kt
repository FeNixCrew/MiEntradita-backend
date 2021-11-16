package ar.edu.unq.mientradita

import com.mercadopago.MercadoPago
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class MientraditaApplication

fun main(args: Array<String>) {
	runApplication<MientraditaApplication>(*args)
}
