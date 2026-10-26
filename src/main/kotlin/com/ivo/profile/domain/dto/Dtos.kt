package com.ivo.profile.domain.dto

data class ProfileDto(
    val name: String = "Ivaylo Blagoev",
    val title: String = "Senior Software Engineer",
    val summary: String = """
        Versatile and results-driven Software Engineer with over a decade of experience across fintech,
        environmental tech, automotive, and betting platforms. Proven expertise in building scalable backend
        systems using Java, Kotlin, Spring Boot, and Kafka. Adept at working in distributed teams, delivering
        secure and compliant solutions, and integrating with complex enterprise ecosystems.
        Passionate about clean architecture, continuous improvement, and solving real-world problems through code.
    """.trimIndent(),
    val email: String = "inblagoev@gmail.com",
    val phone: String = "+359 887 260 937",
    val education: EducationDto = EducationDto(),
    val languages: List<LanguageDto> = listOf(
        LanguageDto("English", "Fluent", 95),
        LanguageDto("Bulgarian", "Native", 100),
        LanguageDto("French", "Beginner", 20)
    ),
    val softSkills: List<String> = listOf(
        "Clear communication",
        "Problem solving",
        "Teamwork",
        "Adaptability",
        "Autonomy",
        "Cultural & professional flexibility",
        "Knowledge sharing"
    )
)

data class EducationDto(
    val institution: String = "George Mason University, USA",
    val degree: String = "Bachelor of Science in Computer Science",
    val period: String = "2007 – 2011"
)

data class LanguageDto(
    val name: String,
    val level: String,
    val percentage: Int
)

data class EventRequest(
    val message: String
)

data class KafkaEventDto(
    val id: Long,
    val topic: String,
    val message: String,
    val direction: String,
    val timestamp: String,
    val status: String
)

data class ExperienceDto(
    val id: Long,
    val title: String,
    val company: String,
    val location: String,
    val startDate: String,
    val endDate: String,
    val description: String,
    val technologies: List<String>
)

data class SkillDto(
    val id: Long,
    val name: String,
    val category: String,
    val proficiencyLevel: Int
)
