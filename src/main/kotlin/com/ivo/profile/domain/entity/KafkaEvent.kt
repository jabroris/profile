package com.ivo.profile.domain.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "kafka_events")
data class KafkaEvent(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val topic: String,

    @Column(columnDefinition = "TEXT")
    val message: String,

    /** SENT or RECEIVED */
    val direction: String,

    val timestamp: LocalDateTime = LocalDateTime.now(),

    val status: String = "OK"
)
