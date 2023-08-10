package org.example.detekt.metrics

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction

class BrainMethodCount(private val config: Config?) : DetektVisitor() {
    var brainMethodCount: Int = 0
        private set

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
            brainMethodCount++
        }
    }

    private fun isDetected(
        amountLineOfCode: Int,
        amountCyclo: Int,
        amountMaxNesting: Int,
        amountNumberOfAccessedVariable: Int
    ) : Boolean {
        val thresholdLOC = 130/2
        val thresholdCYCLOPerLOC = 0.24
        val thresholdMAXNESTING = 5
        val thresholdNOAV = 8

        return (amountLineOfCode > thresholdLOC)
                &&
                (amountCyclo/amountLineOfCode >= thresholdCYCLOPerLOC)
                &&
                (amountMaxNesting >= thresholdMAXNESTING)
                &&
                (amountNumberOfAccessedVariable > thresholdNOAV)
    }

    companion object {
        fun calculate(node: KtElement): Int {
            val visitor = BrainMethodCount(null)
            node.accept(visitor)
            return visitor.brainMethodCount
        }
    }
}
