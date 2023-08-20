package org.example.detekt.metrics

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement

class AccessToForeignDataDev(private val config: Config?) : DetektVisitor()  {

    val foreignDataAccesses = mutableSetOf<String>()

    override fun visitElement(element: PsiElement) {
        super.visitElement(element)

        if (element.toString() == "DOT_QUALIFIED_EXPRESSION"){
            foreignDataAccesses.add(element.text)
        }
    }

    companion object {
        fun calculate(node: KtElement): Int{
            val visitor = AccessToForeignDataDev(null)
            node.accept(visitor)
            return visitor.foreignDataAccesses.size
        }
    }
}
