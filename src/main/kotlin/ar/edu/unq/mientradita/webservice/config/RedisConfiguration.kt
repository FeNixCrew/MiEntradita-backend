package ar.edu.unq.mientradita.webservice.config

import ar.edu.unq.mientradita.service.MailSenderService
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.web.client.RestTemplate

@Configuration
class RedisConfig {

    @Bean
    fun lettuceConnectionFactory(): LettuceConnectionFactory {
        val env = System.getenv()
        val redisStandaloneConfiguration =
            RedisStandaloneConfiguration(env.getOrDefault("REDIS_URL","localhost"),
                env.getOrDefault("REDIS_PORT","6379").toInt())
        return LettuceConnectionFactory(redisStandaloneConfiguration)
    }

    @Bean
    fun redisTemplate(lettuceConnectionFactory: LettuceConnectionFactory): RedisTemplate<String, String> {
        val template = RedisTemplate<String, String>()
        template.setConnectionFactory(lettuceConnectionFactory)
        return template
    }

    @Bean
    fun restTemplate(): RestTemplate = RestTemplateBuilder().build()

    @Bean
    fun messageListenerAdapter(receiverService: MailSenderService): MessageListenerAdapter =
        MessageListenerAdapter(receiverService, "onMessage")

    @Bean
    fun redisMessageContainer(
        lettuceConnectionFactory: LettuceConnectionFactory,
        newReviewListenerAdapter: MessageListenerAdapter,
        receiverService: MailSenderService
    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(lettuceConnectionFactory)
        container.addMessageListener(messageListenerAdapter(receiverService), PatternTopic(CHANNEL))
        return container
    }

}

const val CHANNEL = "new-match"