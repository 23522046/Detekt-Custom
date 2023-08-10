package org.example.detekt

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

class TooManyFuns(config: Config) : Rule(config) {
    override val issue: Issue = Issue(javaClass.simpleName,
        Severity.CodeSmell,
        "This rule reports a file with an excessive function count.",
        Debt.TWENTY_MINS)

    val threshold = valueOrDefault("threshold", 10)
    private var amount: Int = 0

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        if (amount > threshold){
            report(CodeSmell(issue, Entity.from(file), "Too many functions can make the maintainability of a file costlier"))
        }
        amount = 0
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        amount++
    }
}
