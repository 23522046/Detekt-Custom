package org.example.detekt.fowler

import io.gitlab.arturbosch.detekt.api.*
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import org.example.detekt.metrics.linesOfCode
import org.jetbrains.kotlin.com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import java.util.IdentityHashMap

class LongMethod(config: Config = Config.empty) : Rule(config)  {
    override val issue = Issue(
        "LongMethod",
        Severity.Maintainability,
        "One method should have one responsibility. Long methods tend to handle many things at once. \" +\n" +
                "            \"Prefer smaller methods to make them easier to understand.",
        Debt.TWENTY_MINS
    )

    @Configuration("number of lines in a method to trigger the rule")
    private val threshold: Int by config(defaultValue = 60)

    private val functionToLinesCache = HashMap<KtNamedFunction, Int>()
    private val functionToBodyLinesCache = HashMap<KtNamedFunction, Int>()
    private val nestedFunctionTracking = IdentityHashMap<KtNamedFunction, HashSet<KtNamedFunction>>()

    override fun preVisit(root: KtFile) {
        functionToLinesCache.clear()
        functionToBodyLinesCache.clear()
        nestedFunctionTracking.clear()
    }

    override fun postVisit(root: KtFile) {
        val functionToLines = HashMap<KtNamedFunction, Int>()
        functionToBodyLinesCache.map { (function, lines) ->
            val isNested = function.getStrictParentOfType<KtNamedFunction>() != null
            if (isNested){
                functionToLines[function] = functionToLinesCache[function] ?: 0
            } else {
                functionToLines[function] = lines
            }
        }

        for ((function, lines) in functionToLines){
            if (lines >= threshold){
                report(
                    ThresholdedCodeSmell(
                        issue, Entity.atName(function),
                        Metric("SIZE", lines, threshold),
                        "The function ${function.nameAsSafeName} is too long ($lines). The maximum length is $threshold"
                    )
                )
            }
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        val parentMethods = function.getStrictParentOfType<KtNamedFunction>()
        val bodyEntity = function.bodyBlockExpression ?: function.bodyExpression
        val lines = (if (parentMethods != null) function else bodyEntity)?.linesOfCode() ?: 0

        functionToLinesCache[function] = lines
        functionToBodyLinesCache[function] = bodyEntity?.linesOfCode() ?: 0
        parentMethods?.let { nestedFunctionTracking.getOrPut(it) { HashSet() }.add(function) }
        super.visitNamedFunction(function)

        PsiTreeUtil.findChildrenOfType(function, KtNamedFunction::class.java)
            .fold(0) { acc, next -> acc + (functionToLinesCache[next] ?: 0) }
            .takeIf { it > 0 }
            ?.let { functionToLinesCache[function] = lines - it }
    }
}
