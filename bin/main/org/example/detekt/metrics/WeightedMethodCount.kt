package org.example.detekt.metrics

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction

class WeightedMethodCount(private val config: Config?) : DetektVisitor()  {
    var wmcCount: Int = 0
        private set

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        val CC = CyclomaticComplexity.calculate(function){
            this.ignoreSimpleWhenEntries = false
            this.ignoreNestingFunctions = false
            this.nestingFunctions = CyclomaticComplexity.DEFAULT_NESTING_FUNCTIONS
        }

//        println("${function.name} : $CC")
        wmcCount += CC
    }

    companion object {
        fun calculate(node: KtElement): Int {
            val visitor = WeightedMethodCount(null)
            node.accept(visitor)
            return visitor.wmcCount
        }
    }
}
