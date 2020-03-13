package com.algocup.codegeneration

import com.algocup.domain.Function

interface JvmCodeGenerator {

    fun generateEmptyFunction(function: Function): String
}

