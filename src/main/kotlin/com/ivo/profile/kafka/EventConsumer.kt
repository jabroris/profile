package com.ivo.profile.kafka

import com.ivo.profile.config.KafkaConfig
import com.ivo.profile.domain.entity.KafkaEvent
import com.ivo.profile.service.EventService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class EventConsumer(private val eventService: EventService) {

    private val log = LoggerFactory.getLogger(EventConsumer::class.java)

    @KafkaListener(
        topics = [KafkaConfig.TOPIC_PORTFOLIO_EVENTS],
        groupId = "portfolio-group"
    )
    fun consume(message: String) {
        log.info("Received Kafka message: {}", message)
        eventService.save(
            KafkaEvent(
                topic = KafkaConfig.TOPIC_PORTFOLIO_EVENTS,
                message = message,
                direction = "RECEIVED",
                timestamp = LocalDateTime.now(),
                status = "OK"
            )
        )
    }
}
