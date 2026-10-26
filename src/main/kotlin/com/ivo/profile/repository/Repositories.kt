package com.ivo.profile.repository

import com.ivo.profile.domain.entity.Experience
import com.ivo.profile.domain.entity.KafkaEvent
import com.ivo.profile.domain.entity.Skill
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExperienceRepository : JpaRepository<Experience, Long> {
    fun findAllByOrderByDisplayOrderAsc(): List<Experience>
    fun findByTechnologiesContainingIgnoreCase(technology: String): List<Experience>
}

@Repository
interface SkillRepository : JpaRepository<Skill, Long> {
    fun findByCategoryOrderByProficiencyLevelDesc(category: String): List<Skill>
    fun findAllByOrderByCategoryAscProficiencyLevelDesc(): List<Skill>
}

@Repository
interface KafkaEventRepository : JpaRepository<KafkaEvent, Long> {
    fun findTop20ByOrderByTimestampDesc(): List<KafkaEvent>
    fun findByDirection(direction: String): List<KafkaEvent>
}
