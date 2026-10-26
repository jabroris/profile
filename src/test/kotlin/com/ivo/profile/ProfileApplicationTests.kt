package com.ivo.profile

import com.ivo.profile.service.ExperienceService
import com.ivo.profile.service.ProfileService
import com.ivo.profile.service.SkillService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.junit.jupiter.api.Assertions.*

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = [
    "spring.kafka.bootstrap-servers=localhost:19092",  // non-existent – prevents connection
    "spring.kafka.listener.missing-topics-fatal=false",
    "spring.kafka.producer.properties.max.block.ms=100",
    "spring.kafka.producer.retries=0"
])
class ProfileApplicationTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var profileService: ProfileService

    @Autowired
    lateinit var experienceService: ExperienceService

    @Autowired
    lateinit var skillService: SkillService

    @Test
    fun `context loads`() {
        assertNotNull(mockMvc)
    }

    @Test
    fun `profile API returns 200`() {
        mockMvc.get("/api/profile").andExpect {
            status { isOk() }
            jsonPath("$.name") { value("Ivaylo Blagoev") }
        }
    }

    @Test
    fun `experience API returns list`() {
        mockMvc.get("/api/experience").andExpect {
            status { isOk() }
            jsonPath("$") { isArray() }
        }
    }

    @Test
    fun `skills API returns list`() {
        mockMvc.get("/api/skills").andExpect {
            status { isOk() }
            jsonPath("$") { isArray() }
        }
    }

    @Test
    fun `resume page returns 200`() {
        mockMvc.get("/").andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `actuator health returns 200`() {
        mockMvc.get("/actuator/health").andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `profile service returns correct data`() {
        val profile = profileService.getProfile()
        assertEquals("Ivaylo Blagoev", profile.name)
        assertEquals("inblagoev@gmail.com", profile.email)
        assertTrue(profile.softSkills.isNotEmpty())
    }

    @Test
    fun `experience service returns seeded data`() {
        val experiences = experienceService.findAll()
        assertTrue(experiences.isNotEmpty())
        assertTrue(experiences.any { it.company.contains("CreateFuture") })
    }

    @Test
    fun `skill service returns seeded data`() {
        val skills = skillService.findAll()
        assertTrue(skills.isNotEmpty())
        assertTrue(skills.any { it.name == "Kotlin" })
        assertTrue(skills.any { it.name == "Apache Kafka" })
    }

    @Test
    fun `experience search by technology works`() {
        val results = experienceService.findByTechnology("Kafka")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun `swagger UI is accessible`() {
        mockMvc.get("/swagger-ui.html").andExpect {
            // Redirects to swagger-ui/index.html
            status { is3xxRedirection() }
        }
    }
}
