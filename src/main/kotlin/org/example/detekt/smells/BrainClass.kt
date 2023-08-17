package org.example.detekt.smells

import io.gitlab.arturbosch.detekt.api.*
import org.example.detekt.metrics.BrainMethodCount
import org.example.detekt.metrics.TightClassCohesion
import org.example.detekt.metrics.WeightedMethodCount
import org.example.detekt.metrics.linesOfCode
import org.jetbrains.kotlin.psi.KtClass

class BrainClass(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.CodeSmell,
        "Brain class has at least a few methods affected by Brain Method",
        Debt.TWENTY_MINS
    )

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        val amountBrainMethod = BrainMethodCount.calculate(klass)
        val amountLineOfCode = klass.linesOfCode()
        val amountWeightedMethodCount = WeightedMethodCount.calculate(klass)
        val amountTightClassCohesion = TightClassCohesion.calculate(klass)

        if (isDetected(amountBrainMethod, amountLineOfCode, amountWeightedMethodCount, amountTightClassCohesion)){
            report(CodeSmell(issue, Entity.from(klass), "Class ${klass.name} appears to be Brain Class"))
        }
    }

    companion object {
        fun isDetected(
            amountBrainMethod: Int,
            amountLineOfCode: Int,
            amountWeightedMethodCount: Int,
            amountTightClassCohesion: Double
        ): Boolean {
            val thresholdLOCVeryHigh = 195 // VERY HIGH
            val thresholdWMCVeryHigh = 47 // VERY HIGH
            val thresholdTCCHalf = 1.0/2.0 // HALF

            if (amountBrainMethod>1 && amountLineOfCode >= thresholdLOCVeryHigh){
                return true
            } else if (amountBrainMethod==1 && amountLineOfCode >= 2*thresholdLOCVeryHigh && amountWeightedMethodCount >= 2*thresholdWMCVeryHigh){
                return true
            } else if (amountWeightedMethodCount >= thresholdWMCVeryHigh && amountTightClassCohesion < thresholdTCCHalf){
                return true
            }

            return false
        }
    }

}
