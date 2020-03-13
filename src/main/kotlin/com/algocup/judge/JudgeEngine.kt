package com.algocup.judge

import com.algocup.domain.Submission
import com.algocup.domain.JudgeResult
import com.algocup.domain.Problem

interface JudgeEngine {
    fun judge(problem: Problem, submission: Submission): JudgeResult
}
