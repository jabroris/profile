package com.ivo.profile.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaConfig {
    companion object {
        const val TOPIC_PORTFOLIO_EVENTS = "portfolio-events"
    }

    @Bean
    fun portfolioEventsTopic(): NewTopic =
        TopicBuilder
            .name(TOPIC_PORTFOLIO_EVENTS)
            .partitions(3)
            .replicas(1)
            .build()
}
