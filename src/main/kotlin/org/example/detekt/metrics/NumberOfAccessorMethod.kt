package org.example.detekt.metrics

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction

class NumberOfAccessorMethod(private val config: Config?) : DetektVisitor() {
    var accessorMethods = mutableListOf<KtElement>()
    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        val isGetter = function.name?.startsWith("get") ?: false
        val isSetter = function.name?.startsWith("set") ?: false
        if (isSetter || isGetter) accessorMethods.add(function)
    }

    companion object {
        fun calculate(node: KtElement): Int {
            val visitor = NumberOfAccessorMethod(null)
            node.accept(visitor)
            return visitor.accessorMethods.size
        }
    }
}
