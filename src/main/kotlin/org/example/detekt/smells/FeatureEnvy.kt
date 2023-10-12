package org.example.detekt.smells

import io.gitlab.arturbosch.detekt.api.*
import org.example.detekt.metrics.AccessToForeignDataDev
import org.example.detekt.metrics.ForeignDataProviders
import org.example.detekt.metrics.LocalityOfAttributeAccesses
import org.jetbrains.kotlin.psi.KtNamedFunction

/*
    Smell Rule berhasil dibuat, tapi coba cek lagi metrik FDP apakah sudah sesuai? saat ini FDP menghitung jumlah class yang digunakan dari local variable class di dalam method
 */
class FeatureEnvy(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.CodeSmell,
        "Feature Envy refers to methods that seem more interested in the data of other classes than that of their own class",
        Debt.TEN_MINS
    )

    val thresholdATFD = valueOrDefault("thresholdATFDFew", 3) // FEW
    val thresholdLAA = valueOrDefault("thresholdLAAOneThird", 1.0/3.0)
    val thresholdFDP = valueOrDefault("thresholdFDPFew", 3) // FEW

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        val amountATFD = AccessToForeignDataDev.calculate(function)
        val amountLAA = LocalityOfAttributeAccesses.calculate(function)
        val amountFDP = ForeignDataProviders.calculate(function)

        if (isDetected(amountATFD, amountLAA, amountFDP)){
            report(CodeSmell(issue, Entity.atName(function), "The function ${function.name} appears to be Feature Envy\nATFD : ${amountATFD}; LAA : ${amountLAA}; FDP : ${amountFDP}"))
        }
    }

    fun isDetected(amountATFD: Int, amountLAA: Double, amountFDP: Int): Boolean {
        val methodUseDirectlyMoreThanFewAttrsFromOtherClasses = amountATFD > thresholdATFD
        val methodUsesFarMoreAttrsOfOtherClasses = amountLAA < thresholdLAA
        val foreignAttrsBeingUsedBelongToVeryFewClasses = amountFDP <= thresholdFDP

        return (methodUseDirectlyMoreThanFewAttrsFromOtherClasses &&
                methodUsesFarMoreAttrsOfOtherClasses &&
                foreignAttrsBeingUsedBelongToVeryFewClasses)
    }
}
