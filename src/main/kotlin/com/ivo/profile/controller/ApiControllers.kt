package com.ivo.profile.controller

import com.ivo.profile.domain.dto.EventRequest
import com.ivo.profile.domain.dto.ExperienceDto
import com.ivo.profile.domain.dto.KafkaEventDto
import com.ivo.profile.domain.dto.ProfileDto
import com.ivo.profile.domain.dto.SkillDto
import com.ivo.profile.kafka.EventProducer
import com.ivo.profile.service.EventService
import com.ivo.profile.service.ExperienceService
import com.ivo.profile.service.ProfileService
import com.ivo.profile.service.SkillService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profile", description = "Personal profile information")
class ProfileApiController(
    private val profileService: ProfileService,
) {
    @GetMapping
    @Operation(summary = "Get full profile", description = "Returns Ivaylo's profile data from the CV")
    fun getProfile(): ResponseEntity<ProfileDto> = ResponseEntity.ok(profileService.getProfile())
}

@RestController
@RequestMapping("/api/experience")
@Tag(name = "Experience", description = "Work experience backed by Spring Data JPA + H2")
class ExperienceApiController(
    private val experienceService: ExperienceService,
) {
    @GetMapping
    @Operation(summary = "List all experiences", description = "Returns work history ordered by display order")
    fun getAll(): ResponseEntity<List<ExperienceDto>> = ResponseEntity.ok(experienceService.findAll())

    @GetMapping("/search")
    @Operation(summary = "Search by technology", description = "Filter experiences by technology keyword (e.g. Kafka, Kotlin)")
    fun searchByTech(
        @RequestParam tech: String,
    ): ResponseEntity<List<ExperienceDto>> = ResponseEntity.ok(experienceService.findByTechnology(tech))
}

@RestController
@RequestMapping("/api/skills")
@Tag(name = "Skills", description = "Technical skills stored in H2 database")
class SkillApiController(
    private val skillService: SkillService,
) {
    @GetMapping
    @Operation(summary = "List all skills", description = "Returns all technical skills grouped by category")
    fun getAll(): ResponseEntity<List<SkillDto>> = ResponseEntity.ok(skillService.findAll())

    @GetMapping("/category/{category}")
    @Operation(summary = "Get skills by category", description = "Filter skills by category (Language, Framework, Architecture, etc.)")
    fun getByCategory(
        @PathVariable category: String,
    ): ResponseEntity<List<SkillDto>> = ResponseEntity.ok(skillService.findByCategory(category))
}

@RestController
@RequestMapping("/api/events")
@Tag(name = "Kafka Events", description = "Demonstrates Apache Kafka event-driven messaging")
class EventApiController(
    private val eventService: EventService,
    private val eventProducer: EventProducer,
) {
    @GetMapping
    @Operation(
        summary = "List recent events",
        description = "Returns the last 20 Kafka events (sent and received) persisted in H2",
    )
    fun getRecent(): ResponseEntity<List<KafkaEventDto>> = ResponseEntity.ok(eventService.findRecent())

    @PostMapping("/publish")
    @Operation(
        summary = "Publish a Kafka event",
        description =
            "Sends a message to the 'portfolio-events' Kafka topic. " +
                "If Kafka is not running, the event is stored locally with status KAFKA_UNAVAILABLE. " +
                "Start Kafka with: docker-compose up",
    )
    fun publish(
        @RequestBody request: EventRequest,
    ): ResponseEntity<Map<String, String>> {
        val result = eventProducer.send(request.message)
        return ResponseEntity.ok(mapOf("result" to result))
    }
}
