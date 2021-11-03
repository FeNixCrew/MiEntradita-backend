package ar.edu.unq.mientradita.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ReminderService {
    @Autowired
    lateinit var mailSenderService: MailSenderService

    private var isFirstWork: Boolean = true

    private var now: LocalDateTime = LocalDateTime.now()

    /**
     * Our ReminderService works after 5 seconds app starts, and redo this every 24 hours.
     * If we want to do this task in a specific time, use cron parameter into @Scheduled annotation
     * For example:
     * @Scheduled(cron = "0 15 8 * * ? ")
     * This will execute work everyday at 8:15 A.M.
     */
    @Scheduled(initialDelay = 5000, fixedDelay = 36000 * 24)
    fun reminderToSpectators() {
        now = LocalDateTime.now()
        doReminderIfFirstWork()
        remindFor(now)
    }

    private fun doReminderIfFirstWork() {
        if (isFirstWork) {
            remindFor(now.minusDays(1))
            isFirstWork = false
        }
    }

    private fun remindFor(dateTime: LocalDateTime) {
        mailSenderService.writeRemindsToUsersFrom(dateTime)
    }
}