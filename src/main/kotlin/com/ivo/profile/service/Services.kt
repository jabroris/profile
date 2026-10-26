package com.ivo.profile.service

import com.ivo.profile.domain.dto.*
import com.ivo.profile.domain.entity.Experience
import com.ivo.profile.domain.entity.KafkaEvent
import com.ivo.profile.domain.entity.Skill
import com.ivo.profile.repository.ExperienceRepository
import com.ivo.profile.repository.KafkaEventRepository
import com.ivo.profile.repository.SkillRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.format.DateTimeFormatter

@Service
class ProfileService {
    fun getProfile(): ProfileDto = ProfileDto()
}

@Service
@Transactional(readOnly = true)
class ExperienceService(private val repo: ExperienceRepository) {

    fun findAll(): List<ExperienceDto> =
        repo.findAllByOrderByDisplayOrderAsc().map { it.toDto() }

    fun findByTechnology(tech: String): List<ExperienceDto> =
        repo.findByTechnologiesContainingIgnoreCase(tech).map { it.toDto() }

    private fun Experience.toDto() = ExperienceDto(
        id = id,
        title = title,
        company = company,
        location = location,
        startDate = startDate,
        endDate = endDate,
        description = description,
        technologies = technologies
    )
}

@Service
@Transactional(readOnly = true)
class SkillService(private val repo: SkillRepository) {

    fun findAll(): List<SkillDto> =
        repo.findAllByOrderByCategoryAscProficiencyLevelDesc().map { it.toDto() }

    fun findByCategory(category: String): List<SkillDto> =
        repo.findByCategoryOrderByProficiencyLevelDesc(category).map { it.toDto() }

    private fun Skill.toDto() = SkillDto(
        id = id,
        name = name,
        category = category,
        proficiencyLevel = proficiencyLevel
    )
}

@Service
@Transactional(readOnly = true)
class EventService(private val repo: KafkaEventRepository) {

    fun findRecent(): List<KafkaEventDto> =
        repo.findTop20ByOrderByTimestampDesc().map { it.toDto() }

    @Transactional
    fun save(event: KafkaEvent): KafkaEvent = repo.save(event)

    private fun KafkaEvent.toDto() = KafkaEventDto(
        id = id,
        topic = topic,
        message = message,
        direction = direction,
        timestamp = timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
        status = status
    )
}

@Service
class DataInitializer(
    private val experienceRepo: ExperienceRepository,
    private val skillRepo: SkillRepository
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        seedExperiences()
        seedSkills()
    }

    private fun seedExperiences() {
        val experiences = listOf(
            Experience(
                title = "Java/Kotlin Developer",
                company = "CreateFuture",
                location = "Remote",
                startDate = "Jul 2025",
                endDate = "Feb 2026",
                description = "Developed backend services for a high-scale betting platform. Architected and maintained microservices for the Orders Team. Ensured reliable event-driven communication and data consistency across distributed systems.",
                technologies = listOf("Kotlin", "Java", "Spring Boot", "Kafka", "Microservices"),
                displayOrder = 1
            ),
            Experience(
                title = "Software Engineer (Contractor)",
                company = "Paysafe (via Brainstars)",
                location = "Remote, Varna, Bulgaria",
                startDate = "Dec 2024",
                endDate = "Jun 2025",
                description = "Delivered full-stack development for Skrill's KYC application. Built secure and scalable features using Java, Spring Boot, and modern frontend technologies. Collaborated with compliance and product teams to meet regulatory standards.",
                technologies = listOf("Java", "Spring Boot", "Spring Security", "KYC", "Compliance"),
                displayOrder = 2
            ),
            Experience(
                title = "Software Engineer",
                company = "Lever X",
                location = "Sofia, Bulgaria",
                startDate = "Jan 2024",
                endDate = "Aug 2024",
                description = "Designed and maintained enterprise-grade software solutions. Drove full SDLC from requirements analysis to deployment. Contributed to SAP-related projects.",
                technologies = listOf("Java", "Spring", "SAP CAP", "CDS", "Enterprise"),
                displayOrder = 3
            ),
            Experience(
                title = "Software Engineer",
                company = "Industria Tech",
                location = "Sofia, Bulgaria",
                startDate = "Mar 2022",
                endDate = "Dec 2023",
                description = "Developed internal and client-facing solutions. Integrated distributed ledger technologies including Corda and Ethereum.",
                technologies = listOf("Java", "Kotlin", "Spring", "Corda", "Ethereum", "Blockchain"),
                displayOrder = 4
            ),
            Experience(
                title = "Java Engineer",
                company = "Catenate",
                location = "Sofia, Bulgaria",
                startDate = "Aug 2021",
                endDate = "Mar 2022",
                description = "Engineered forecasting and ticketing software for BMW's supplier network. Enabled predictive analytics to identify production bottlenecks.",
                technologies = listOf("Java", "Spring Boot", "Analytics", "Automotive"),
                displayOrder = 5
            ),
            Experience(
                title = "Software Engineer",
                company = "Kipp & Zonen",
                location = "Delft, Netherlands",
                startDate = "Mar 2019",
                endDate = "Mar 2020",
                description = "Built testing software for solar radiation sensors used in climate research. Coordinated product updates with R&D for seamless hardware-software integration.",
                technologies = listOf("Java", "Hardware Integration", "Climate Tech", "IoT"),
                displayOrder = 6
            ),
            Experience(
                title = "Software Engineer",
                company = "Sutron Corporation",
                location = "Sterling, VA, USA",
                startDate = "Dec 2011",
                endDate = "Feb 2019",
                description = "Developed and maintained environmental data acquisition systems (DAS, DCS Toolkit). Led development and deployment of hydrological platforms (hydrometcloud.com, sutronwin.com). Conducted international field deployments and client training sessions.",
                technologies = listOf("Java", "Environmental Systems", "Data Acquisition", "Cloud"),
                displayOrder = 7
            )
        )
        experienceRepo.saveAll(experiences)
    }

    private fun seedSkills() {
        val skills = listOf(
            // Languages
            Skill(name = "Kotlin", category = "Language", proficiencyLevel = 5),
            Skill(name = "Java", category = "Language", proficiencyLevel = 5),
            Skill(name = "SQL", category = "Language", proficiencyLevel = 4),
            // Frameworks
            Skill(name = "Spring Boot", category = "Framework", proficiencyLevel = 5),
            Skill(name = "Spring Security", category = "Framework", proficiencyLevel = 4),
            Skill(name = "Spring Data JPA", category = "Framework", proficiencyLevel = 5),
            Skill(name = "Spring Kafka", category = "Framework", proficiencyLevel = 4),
            // Messaging
            Skill(name = "Apache Kafka", category = "Messaging", proficiencyLevel = 4),
            Skill(name = "Event-Driven Architecture", category = "Messaging", proficiencyLevel = 4),
            // Architecture
            Skill(name = "Microservices", category = "Architecture", proficiencyLevel = 5),
            Skill(name = "REST APIs", category = "Architecture", proficiencyLevel = 5),
            Skill(name = "Clean Architecture", category = "Architecture", proficiencyLevel = 4),
            // Tools
            Skill(name = "Docker", category = "DevOps", proficiencyLevel = 4),
            Skill(name = "Git", category = "DevOps", proficiencyLevel = 5),
            Skill(name = "Gradle / Maven", category = "DevOps", proficiencyLevel = 4),
            // Databases
            Skill(name = "PostgreSQL", category = "Database", proficiencyLevel = 4),
            Skill(name = "H2", category = "Database", proficiencyLevel = 4),
            // Blockchain
            Skill(name = "Corda", category = "Blockchain", proficiencyLevel = 3),
            Skill(name = "Ethereum", category = "Blockchain", proficiencyLevel = 3)
        )
        skillRepo.saveAll(skills)
    }
}
