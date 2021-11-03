package ar.edu.unq.mientradita.service

import ar.edu.unq.mientradita.model.exception.format
import ar.edu.unq.mientradita.model.user.Spectator
import ar.edu.unq.mientradita.persistence.match.MailAndMatch
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.mail.MessagingException


@Service
class MailSenderService: MessageListener {

    @Autowired
    private lateinit var javaMailSender: JavaMailSender

    @Autowired
    private lateinit var spectatorService: SpectatorService

    @Autowired
    private lateinit var matchService: MatchService

    @Autowired
    private lateinit var objectMapper: ObjectMapper


    override fun onMessage(message: Message, pattern: ByteArray?) {
        notifyToFansFrom(getMatchFrom(message))
    }

    fun writeRemindsToUsersFrom(dateTime: LocalDateTime) {
        matchService.rememberOf(dateTime).forEach { mailAndMatch -> sendReminder(mailAndMatch) }
    }

    private fun sendReminder(mailAndMatch: MailAndMatch) {
        val mailMessage = javaMailSender.createMimeMessage()
        val helper: MimeMessageHelper
        try {
            val match = mailAndMatch.match

            helper = MimeMessageHelper(mailMessage, false)
            helper.setTo(mailAndMatch.mail)
            helper.setSubject("[Recordatorio] $match, ${format(match.matchStartTime)}")
            helper.setText(
                """
                   <html>
                    <body>
                        <div>
                            <div> Estimado,</div>
                            <div> Este correo es un recordatorio del encuentro que se va a 
                            disputar el <strong>${format(match.matchStartTime)}, ${hour(match.matchStartTime)}</strong>,
                            en el cual usted tiene una entrada reservada.</div>
                        </div>
                        <div>Recuerde llevar el QR de la entrada que se genera en nuestra aplicacion para poder 
                        ingresar al encuentro.</div>
                        <div>Esperamos que disfrute del partido.</div>
                        <div>Saludos.</div>
                    </body>
                   </html>
                """.trimIndent(), true
            )
            javaMailSender.send(mailMessage)
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }

    private fun notifyToFansFrom(matchDTO: MatchDTO) {
        spectatorService.fansFrom(matchDTO.id).forEach { notifyToFan(it, matchDTO) }
    }

    private fun notifyToFan(spectator: Spectator, matchDTO: MatchDTO) {
        val mailMessage = javaMailSender.createMimeMessage()
        val helper: MimeMessageHelper
        try {
            helper = MimeMessageHelper(mailMessage, false)
            helper.setTo(spectator.email)
            helper.setSubject("¡Entradas para un nuevo partido ya disponibles!")
            helper.setText(
                """
                   <html>
                    <body>
                        <div>
                            <div> Hola ${spectator.username},</div>
                            <div> Tenemos el agrado de comunicarle que ya se encuentran disponibles las entradas para un nuevo partido de tu equipo favorito.</div>
                                <div>${showConditions(spectator, matchDTO)}</div>
                            <div>El encuentro se disputará el <strong>${format(matchDTO.matchStartTime)}, ${hour(matchDTO.matchStartTime)}</strong>.</div>
                        </div>
                        <div>Saludos.</div>
                    </body>
                   </html>
                """.trimIndent(), true
            )
            javaMailSender.send(mailMessage)
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }

    private fun getMatchFrom(message: Message): MatchDTO {
        val jsonMatchDTO: String = message.body.decodeToString().substring(7)
        return objectMapper.readValue(jsonMatchDTO, MatchDTO::class.java)
    }

    private fun showConditions(spectator: Spectator, matchDTO: MatchDTO): String {
        return if (spectator.favouriteTeam!!.name == matchDTO.home) {
            "${matchDTO.home} jugará en condición de LOCAL contra ${matchDTO.away}"
        } else {
            "${matchDTO.away} jugará en condición de VISITANTE contra ${matchDTO.home}"
        }
    }

    private fun hour(matchStartTime: LocalDateTime): String {
        return "${matchStartTime.hour}:${matchStartTime.minute}Hs"
    }
}