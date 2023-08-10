package org.example.detekt.metrics

import io.gitlab.arturbosch.detekt.api.*
import io.gitlab.arturbosch.detekt.rules.isUsedForNesting
import org.jetbrains.kotlin.psi.*

class MaximumNesting(private val config: Config?) : DetektVisitor() {

    var depth: Int = 0
        private set

    private fun inc(){
        depth++
    }

    private fun dec(){
        depth--
    }

    override fun visitIfExpression(expression: KtIfExpression) {
        println(expression.toString())
        // Prevents else if (...) to count as two
        if (expression.parent !is KtContainerNodeForControlStructureBody){
            inc()
            super.visitIfExpression(expression)
            dec()
        } else {
            super.visitIfExpression(expression)
        }
    }

    override fun visitLoopExpression(loopExpression: KtLoopExpression) {
        inc()
        super.visitLoopExpression(loopExpression)
        dec()
    }

    override fun visitWhenExpression(expression: KtWhenExpression) {
        inc()
        super.visitWhenExpression(expression)
        dec()
    }

    override fun visitTryExpression(expression: KtTryExpression) {
        inc()
        super.visitTryExpression(expression)
        dec()
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        val lambdaArguments = expression.lambdaArguments
        if (expression.isUsedForNesting()){
            insideLambdaDo(lambdaArguments){ inc()}
            super.visitCallExpression(expression)
            insideLambdaDo(lambdaArguments){ dec()}
        }
    }

    private fun insideLambdaDo(lambdaArguments: List<KtLambdaArgument>, function: () -> Unit) {
        if (lambdaArguments.isNotEmpty()){
            val lambdaArgument = lambdaArguments[0]
            if (lambdaArgument.getLambdaExpression()?.bodyExpression != null){
                function()
            }
        }
    }

    companion object {

        fun calculate(node: KtElement): Int {
            val visitor = MaximumNesting(null)
            node.accept(visitor)
            return visitor.depth
        }
    }

}
