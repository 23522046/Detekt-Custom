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

    val thresholdLOCVeryHigh = valueOrDefault("thresholdLOCVeryHigh", 195) // VERY HIGH
    val thresholdWMCVeryHigh = valueOrDefault("thresholdWMCVeryHigh", 47) // VERY HIGH
    val thresholdTCCHalf = valueOrDefault("thresholdTCCHalf", 1.0/2.0) // HALF

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        val amountBrainMethod = BrainMethodCount.calculate(klass)
        val amountLineOfCode = klass.linesOfCode()
        val amountWeightedMethodCount = WeightedMethodCount.calculate(klass)
        val amountTightClassCohesion = TightClassCohesion.calculate(klass)

        if (isDetected(amountBrainMethod, amountLineOfCode, amountWeightedMethodCount, amountTightClassCohesion)){
            val newLine = System.lineSeparator()
            report(CodeSmell(issue, Entity.from(klass), "${newLine}Class ${klass.name} appears to be Brain Class ${newLine}[BrainMethod : $amountBrainMethod; LOC : $amountLineOfCode; WMC : $amountWeightedMethodCount; TCC : $amountTightClassCohesion]"))
        }
    }

    fun isDetected(
        amountBrainMethod: Int,
        amountLineOfCode: Int,
        amountWeightedMethodCount: Int,
        amountTightClassCohesion: Double
    ): Boolean {
        val classContainMoreThanOneBrainMethod = amountBrainMethod > 1
        val totalMethodSizeInClassIsVeryHigh = amountLineOfCode >= thresholdLOCVeryHigh
        val moreThanOneBrainMethodAndMethodsVeryHigh = classContainMoreThanOneBrainMethod
                && totalMethodSizeInClassIsVeryHigh

        val classContainOnlyOneBrainMethod = amountBrainMethod==1
        val totalMethodSizeInClassIsExtremelyHigh = amountLineOfCode >= 2*thresholdLOCVeryHigh
        val functionalComplexityOfClassIsExtremelyHigh = amountWeightedMethodCount >= 2*thresholdWMCVeryHigh
        val onlyOneBrainMethodButExtremlyLargeAndComplex = classContainOnlyOneBrainMethod &&
                totalMethodSizeInClassIsExtremelyHigh &&
                functionalComplexityOfClassIsExtremelyHigh

        val functionalComplexityOfClassIsVeryHigh = amountWeightedMethodCount >= thresholdWMCVeryHigh
        val classCohesionIsLow = amountTightClassCohesion < thresholdTCCHalf
        val classIsVeryComplexAndNonCohesive = functionalComplexityOfClassIsVeryHigh && classCohesionIsLow

        val isBrainClass = ((moreThanOneBrainMethodAndMethodsVeryHigh || onlyOneBrainMethodButExtremlyLargeAndComplex)
                && classIsVeryComplexAndNonCohesive)

        return isBrainClass
    }

}
