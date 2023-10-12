package org.example.detekt.smells

import io.gitlab.arturbosch.detekt.api.*
import org.example.detekt.metrics.AccessToForeignData
import org.example.detekt.metrics.TightClassCohesion
import org.example.detekt.metrics.WeightedMethodCount
import org.jetbrains.kotlin.psi.KtClass

class GodClass(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.CodeSmell,
        "God Class tend to centralize subsystem or whole system",
        Debt.TWENTY_MINS
    )

    val thresholdATFD = valueOrDefault("thresholdATFDFew", 3) //FEW
    val thresholdWMC = valueOrDefault("thresholdWMCVeryHigh", 47) // VERY HIGH
    val thresholdTCC = valueOrDefault("thresholdTCCOneThird", 1.0/3.0) // ONE THIRD

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        val amountATFD = AccessToForeignData.calculate(klass)
        val amountWMC = WeightedMethodCount.calculate(klass)
        val amountTCC = TightClassCohesion.calculate(klass)

        if (isDetected(amountATFD, amountWMC, amountTCC)){
            val newLine = System.lineSeparator()
            report(CodeSmell(issue, Entity.from(klass), "${newLine}Class ${klass.name} appears to be God Class ${newLine}[ATFD : $amountATFD; WMC : $amountWMC; TCC : $amountTCC]"))
        }
    }

    fun isDetected(atfd: Int, wmc: Int, tcc: Double): Boolean {
        val classUsesMoreThanFewAttrFromOtherClasses = (atfd > thresholdATFD)
        val classHasVeyHighFuncComplexity = (wmc >= thresholdWMC)
        val classCohesionIsLow = (tcc < thresholdTCC)

        return classUsesMoreThanFewAttrFromOtherClasses && classHasVeyHighFuncComplexity && classCohesionIsLow
    }

}
