package org.example.detekt.smells

import io.gitlab.arturbosch.detekt.api.*
import org.example.detekt.metrics.NumberOfAccessorMethod
import org.example.detekt.metrics.NumberOfPublicAttribute
import org.example.detekt.metrics.WeightOfClass
import org.example.detekt.metrics.WeightedMethodCount
import org.jetbrains.kotlin.psi.KtClass

class DataClass(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.CodeSmell,
        "Data classes are dumb data holders without complex functionality but other classes strongly rely on them",
        Debt.TWENTY_MINS
    )

    val thresholdOneThird = valueOrDefault("thresholdWOCOneThird", 1.0 / 3.0)
    val thresholdFew = valueOrDefault("thresholdNOAPNOAMFew", 3)
    val thresholdMany = valueOrDefault("thresholdNOAPNOAMMany", 8)
    val thresholdHighWMC = valueOrDefault("thresholdHighWMC", 31)
    val thresholdVeryHighWMC = valueOrDefault("thresholdVeryHighWMC", 47)

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        val amountWOC = WeightOfClass.calculate(klass)
        val amountWMC = WeightedMethodCount.calculate(klass)
        val amountNOAP = NumberOfPublicAttribute.calculate(klass)
        val amountNOAM = NumberOfAccessorMethod.calculate(klass)

        if (isDetected(amountWOC, amountWMC, amountNOAP, amountNOAM)) {
            report(CodeSmell(issue, Entity.from(klass), "Class ${klass.name} appears to be Data Class"))
        }
    }

    fun isDetected(woc: Double, wmc: Int, noap: Int, noam: Int): Boolean {
        val revealsDataThanService = woc < thresholdOneThird
        val moreThanFewPublicData = noap + noam > thresholdFew
        val complexityNotHigh = wmc < thresholdHighWMC
        val hasManyPublicData = noap + noam > thresholdMany
        val complexityNotVeryHigh = wmc < thresholdVeryHighWMC

        val revealsManyAttributesAndNotComplex =
            (moreThanFewPublicData && complexityNotHigh) && (hasManyPublicData && complexityNotVeryHigh)

        return revealsDataThanService && revealsManyAttributesAndNotComplex
    }
}
