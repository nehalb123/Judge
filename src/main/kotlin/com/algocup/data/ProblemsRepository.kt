package com.algocup.data

import com.algocup.domain.Problem

interface ProblemsRepository {
    fun find(id: String): Problem?
    fun findAll(): List<Problem>
}
