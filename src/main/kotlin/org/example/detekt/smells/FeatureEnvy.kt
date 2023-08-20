package org.example.detekt.smells

import io.gitlab.arturbosch.detekt.api.*
import org.example.detekt.metrics.LocalityOfAttributeAccesses
import org.jetbrains.kotlin.psi.KtNamedFunction

class FeatureEnvy(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.CodeSmell,
        "Feature Envy refers to methods that seem more interested in the data of other classes than that of their own class",
        Debt.TEN_MINS
    )

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        val amountATFD = 0
        val amountLAA = LocalityOfAttributeAccesses.calculate(function)
        val amountFDP = 0

        if (isDetected(amountATFD, amountLAA, amountFDP)){
            report(CodeSmell(issue, Entity.atName(function), "The function ${function.name} appears to be Feature Envy"))
        }
    }

    companion object {
        fun isDetected(amountATFD: Int, amountLAA: Double, amountFDP: Int): Boolean {
            val thresholdATFD = 4 // FEW
            val thresholdLAA = 1.0/3.0
            val thresholdFDP = 4 // FEW

            return (amountATFD > thresholdATFD && amountLAA < thresholdLAA && amountFDP <= thresholdFDP)
        }
    }
}
