package org.example.detekt.metrics

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.example.detekt.smells.BrainMethod
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

        val brainMethod = BrainMethod(Config.empty)
        if (brainMethod.isDetected(amountLineOfCode, amountCyclo, amountMaxNesting, amountNumberOfAccessedVariable)){
            brainMethodCount++
        }
    }

    companion object {
        fun calculate(node: KtElement): Int {
            val visitor = BrainMethodCount(null)
            node.accept(visitor)
            return visitor.brainMethodCount
        }
    }
}
