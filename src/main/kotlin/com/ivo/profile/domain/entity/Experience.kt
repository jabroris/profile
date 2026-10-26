package com.ivo.profile.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "experiences")
data class Experience(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val title: String,
    val company: String,
    val location: String,
    val startDate: String,
    val endDate: String,

    @Column(columnDefinition = "TEXT")
    val description: String,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "experience_technologies", joinColumns = [JoinColumn(name = "experience_id")])
    @Column(name = "technology")
    val technologies: List<String> = emptyList(),

    val displayOrder: Int = 0
)
