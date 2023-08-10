package org.example.detekt.metrics

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor

class WeightMethodCount(private val config: Config?) : DetektVisitor()  {
    
}
