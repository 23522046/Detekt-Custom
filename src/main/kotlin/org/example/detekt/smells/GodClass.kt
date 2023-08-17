package org.example.detekt.smells

import io.gitlab.arturbosch.detekt.api.*
import org.example.detekt.metrics.AccessToForeignData
import org.example.detekt.metrics.TightClassCohesion
import org.example.detekt.metrics.WeightedMethodCount
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile

class GodClass(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.CodeSmell,
        "God Class tend to centralize subsystem or whole system",
        Debt.TWENTY_MINS
    )

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        val amountATFD = AccessToForeignData.calculate(klass)
        val amountWMC = WeightedMethodCount.calculate(klass)
        val amountTCC = TightClassCohesion.calculate(klass)

        if (isDetected(amountATFD, amountWMC, amountTCC)){
            report(CodeSmell(issue, Entity.from(klass), "Class ${klass.name} appears to be God Class"))
        }
    }

    companion object {
        fun isDetected(atfd: Int, wmc: Int, tcc: Double): Boolean {
            val thresholdATFD = 4 //FEW
            val thresholdWMC = 47 // VERY HIGH
            val thresholdTCC = 1.0/3.0 // ONE THIRD

            return (atfd > thresholdATFD) && (wmc >= thresholdWMC) && (tcc < thresholdTCC)
        }
    }

}
