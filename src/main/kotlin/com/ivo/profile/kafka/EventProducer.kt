package com.ivo.profile.kafka

import com.ivo.profile.config.KafkaConfig
import com.ivo.profile.domain.entity.KafkaEvent
import com.ivo.profile.service.EventService
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@Component
class EventProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val eventService: EventService
) {
    private val log = LoggerFactory.getLogger(EventProducer::class.java)

    fun send(message: String): String {
        val topic = KafkaConfig.TOPIC_PORTFOLIO_EVENTS

        return try {
            val future: CompletableFuture<SendResult<String, String>> =
                kafkaTemplate.send(topic, message)

            future.whenComplete { result, ex ->
                if (ex != null) {
                    log.warn("Failed to send Kafka message: {}", ex.message)
                } else {
                    log.info("Sent to topic={} partition={} offset={}",
                        result.recordMetadata.topic(),
                        result.recordMetadata.partition(),
                        result.recordMetadata.offset()
                    )
                }
            }

            eventService.save(
                KafkaEvent(
                    topic = topic,
                    message = message,
                    direction = "SENT",
                    timestamp = LocalDateTime.now(),
                    status = "OK"
                )
            )

            "Message queued for Kafka topic: $topic"
        } catch (ex: Exception) {
            log.warn("Kafka unavailable, storing event locally: {}", ex.message)
            eventService.save(
                KafkaEvent(
                    topic = topic,
                    message = message,
                    direction = "SENT",
                    timestamp = LocalDateTime.now(),
                    status = "KAFKA_UNAVAILABLE"
                )
            )
            "Kafka unavailable – event stored locally (start Kafka with docker-compose up)"
        }
    }
}
