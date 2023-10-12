package org.example.detekt.metrics

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifier

class WeightOfClass(private val config: Config?) : DetektVisitor() {
    val publicMethods = mutableListOf<KtNamedFunction>()
    val publicMembers = mutableListOf<KtElement>()

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        publicMethods.addAll(klass.declarations.filterIsInstance<KtNamedFunction>().filter { it.visibilityModifier()?.text in listOf("public", null) })
        publicMembers.addAll(klass.declarations.filter { it.visibilityModifier()?.text in listOf("public", null) })

//        println(publicMethods.map { it.name })
//        println(publicMembers.map { it.name })
    }

    companion object {
        fun calculate(node: KtElement) : Double {
            val visitor = WeightOfClass(null)
            node.accept(visitor)

            return visitor.publicMethods.size.toDouble() / visitor.publicMembers.size.toDouble()
        }
    }
}
