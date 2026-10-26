package com.ivo.profile.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "skills")
data class Skill(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String,
    val category: String,

    /** Proficiency level 1–5 */
    val proficiencyLevel: Int = 5
)
