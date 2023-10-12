package org.example.detekt.metrics

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifier

class NumberOfPublicAttribute(private val config: Config?) : DetektVisitor() {
    val publicAttributes = mutableListOf<KtProperty>()
    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        publicAttributes.addAll(klass.getProperties().filter { it.visibilityModifier()?.text  in listOf("public", null) })
    }

    companion object {
        fun calculate(node: KtElement): Int {
            val visitor = NumberOfPublicAttribute(null)
            node.accept(visitor)

            return visitor.publicAttributes.size
        }
    }
}
