package org.example.detekt.smells

import io.gitlab.arturbosch.detekt.api.*
import org.example.detekt.metrics.CyclomaticComplexity
import org.example.detekt.metrics.MaximumNesting
import org.example.detekt.metrics.NumberOfAccessedVariables
import org.example.detekt.metrics.linesOfCode
import org.jetbrains.kotlin.psi.KtNamedFunction

class BrainMethod(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.CodeSmell,
        "Brain Methods tend to centralize the functionality of a class",
        Debt.TEN_MINS
    )

    val thresholdLOC = valueOrDefault("thresholdLOCHigh", 130) // HIGH per method
    val thresholdCYCLOPerLOC = valueOrDefault("thresholdCYCLOPerLOCHigh", 0.24) // HIGH
    val thresholdMAXNESTING = valueOrDefault("thresholdMAXNESTINGSeveral", 4) // SEVERAL
    val thresholdNOAV = valueOrDefault("thresholdNOAVMany", 8) // MANY

    override fun visitNamedFunction(function: KtNamedFunction) {

        val amountCyclo = CyclomaticComplexity.calculate(function) {
            this.ignoreSimpleWhenEntries = false
            this.ignoreNestingFunctions = false
            this.nestingFunctions = CyclomaticComplexity.DEFAULT_NESTING_FUNCTIONS
        }
        val amountMaxNesting = MaximumNesting.calculate(function)
        val amountLineOfCode = function.linesOfCode()
        val amountNumberOfAccessedVariable = NumberOfAccessedVariables.calculate(function)

        if (isDetected(amountLineOfCode, amountCyclo, amountMaxNesting, amountNumberOfAccessedVariable)) {
            val newLine = System.lineSeparator()
            report(
                CodeSmell(
                    issue, Entity.atName(function),
                    "${newLine}The function ${function.nameAsSafeName} appears to be Brain Method ${newLine}[CYCLO : $amountCyclo; MAXNESTING : $amountMaxNesting; LOC : $amountLineOfCode; NOAV : $amountNumberOfAccessedVariable]"
                )
            )
        }
    }

    fun isDetected(
        amountLineOfCode: Int,
        amountCyclo: Int,
        amountMaxNesting: Int,
        amountNumberOfAccessedVariable: Int
    ): Boolean {
        val methodIsExcessivelyLarge = amountLineOfCode.toDouble() > thresholdLOC / 2.0
        val methodHasManyConditionalBranches =
            amountCyclo.toDouble() / amountLineOfCode.toDouble() >= thresholdCYCLOPerLOC
        val methodHasDeepNesting = amountMaxNesting >= thresholdMAXNESTING
        val methodUsesManyVariables = amountNumberOfAccessedVariable > thresholdNOAV

        return methodIsExcessivelyLarge &&
                methodHasManyConditionalBranches &&
                methodHasDeepNesting &&
                methodUsesManyVariables
    }

}
