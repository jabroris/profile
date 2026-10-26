package com.ivo.profile.domain.entity

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table

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
    val displayOrder: Int = 0,
)
