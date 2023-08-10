package org.example.detekt

import io.gitlab.arturbosch.detekt.api.*
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import org.jetbrains.kotlin.psi.KtNamedFunction

class CyclomaticComplexity(config: Config) : Rule(config) {
    override val issue = Issue(
        "CyclomaticComplexMethod",
        Severity.Maintainability,
        "Prefer splitting up complex methods into smaller, easier to test methods.",
        Debt.TWENTY_MINS
    )

    override val defaultRuleIdAliases: Set<String> = setOf("ComplexMethod")

    @Configuration("McCabe's Cyclomatic Complexity (MCC) number for a method.")
    private val threshold: Int by config(defaultValue = 15)

    @Configuration("Ignores a complex method if it only contains a single when expression.")
    private val ignoreSingleWhenExpression: Boolean by config(false)

    @Configuration("Whether to ignore simple (braceless) when entries.")
    private val ignoreSimpleWhenEntries: Boolean by config(false)

    @Configuration("Whether to ignore functions which are often used instead of an `if` or `for` statement.")
    private val ignoreNestingFunctions: Boolean by config(false)

    @Configuration("Comma separated list of function names which add complexity.")
    private val nestingFunctions: Set<String> by config(DEFAULT_NESTING_FUNCTIONS) { it.toSet() }

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (ignoreSingleWhenExpression && hasSingleWhenExpression(function.bodyExpression)) {
            return
        }

        val complexity = CyclomaticComplexity.calculate(function) {
            this.ignoreSimpleWhenEntries = this@CyclomaticComplexMethod.ignoreSimpleWhenEntries
            this.ignoreNestingFunctions = this@CyclomaticComplexMethod.ignoreNestingFunctions
            this.nestingFunctions = this@CyclomaticComplexMethod.nestingFunctions
        }

        if (complexity >= threshold) {
            report(
                ThresholdedCodeSmell(
                    issue,
                    Entity.atName(function),
                    Metric("MCC", complexity, threshold),
                    "The function ${function.nameAsSafeName} appears to be too complex " +
                            "based on Cyclomatic Complexity (complexity: $complexity). " +
                            "Defined complexity threshold for methods is set to '$threshold'"
                )
            )
        }
    }

    private fun hasSingleWhenExpression(bodyExpression: KtExpression?): Boolean = when {
        bodyExpression is KtBlockExpression && bodyExpression.statements.size == 1 -> {
            val statement = bodyExpression.statements.single()
            statement is KtWhenExpression || statement.returnsWhenExpression()
        }
        // the case where function-expression syntax is used: `fun test() = when { ... }`
        bodyExpression is KtWhenExpression -> true
        else -> false
    }

    private fun KtExpression.returnsWhenExpression() =
        this is KtReturnExpression && this.returnedExpression is KtWhenExpression

    companion object {
        val DEFAULT_NESTING_FUNCTIONS = listOf(
            "also",
            "apply",
            "forEach",
            "isNotNull",
            "ifNull",
            "let",
            "run",
            "use",
            "with",
        )
    }
}
