package com.algocup.type

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeightedGraphNode(@JvmField var name: String)
