package com.ivo.profile.controller

import com.ivo.profile.service.ExperienceService
import com.ivo.profile.service.ProfileService
import com.ivo.profile.service.SkillService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ResumeController(
    private val profileService: ProfileService,
    private val experienceService: ExperienceService,
    private val skillService: SkillService,
) {
    @GetMapping("/")
    fun resume(model: Model): String {
        val profile = profileService.getProfile()
        val experiences = experienceService.findAll()
        val skills = skillService.findAll()

        val skillsByCategory = skills.groupBy { it.category }

        model.addAttribute("profile", profile)
        model.addAttribute("experiences", experiences)
        model.addAttribute("skills", skills)
        model.addAttribute("skillsByCategory", skillsByCategory)

        return "resume"
    }
}
