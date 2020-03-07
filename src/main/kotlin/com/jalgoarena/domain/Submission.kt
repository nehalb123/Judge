package com.jalgoarena.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class Submission(
        val sourceCode: String,
        val userId: String,
        val submissionId: String,
        val problemId: String,
        val submissionTime: LocalDateTime,
        val id: Int,
        val token: String
)
