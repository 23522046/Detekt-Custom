package org.example.detekt.smells

import io.gitlab.arturbosch.detekt.api.*
import org.example.detekt.metrics.CyclomaticComplexity
import org.example.detekt.metrics.MaximumNesting
import org.example.detekt.metrics.NumberOfAccessedVariables
import org.example.detekt.metrics.linesOfCode
import org.jetbrains.kotlin.psi.KtNamedFunction

// TODO: rule ini hampir selesai, MAXNESTING sudah bisa dilihat nilainya
class BrainMethod(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.CodeSmell,
        "Brain Methods tend to centralize the functionality of a class",
        Debt.TEN_MINS
    )

    override fun visitNamedFunction(function: KtNamedFunction) {

        val amountCyclo = CyclomaticComplexity.calculate(function){
            this.ignoreSimpleWhenEntries = false
            this.ignoreNestingFunctions = false
            this.nestingFunctions = CyclomaticComplexity.DEFAULT_NESTING_FUNCTIONS
        }
        val amountMaxNesting = MaximumNesting.calculate(function)
        val amountLineOfCode = function.linesOfCode()
        val amountNumberOfAccessedVariable = NumberOfAccessedVariables.calculate(function)

        if (isDetected(amountLineOfCode, amountCyclo, amountMaxNesting, amountNumberOfAccessedVariable)){
            report(CodeSmell(issue, Entity.atName(function),
                "The function ${function.nameAsSafeName} appears to be Brain Method"))
        }
    }

    companion object {
        fun isDetected(
            amountLineOfCode: Int,
            amountCyclo: Int,
            amountMaxNesting: Int,
            amountNumberOfAccessedVariable: Int
        ) : Boolean {
            val thresholdLOC = 130.0 // HIGH // 65
            val thresholdCYCLOPerLOC = 0.24 // HIGH
            val thresholdMAXNESTING = 5 // SEVERAL
            val thresholdNOAV = 8 // MANY

            return (amountLineOfCode.toDouble() > thresholdLOC / 2.0)
                    &&
                    (amountCyclo.toDouble()/amountLineOfCode.toDouble() >= thresholdCYCLOPerLOC)
                    &&
                    (amountMaxNesting >= thresholdMAXNESTING)
                    &&
                    (amountNumberOfAccessedVariable > thresholdNOAV)
        }
    }

}
