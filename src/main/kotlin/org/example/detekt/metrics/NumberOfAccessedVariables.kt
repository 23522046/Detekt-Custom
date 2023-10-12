package org.example.detekt.metrics

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*

// TODO: Cek lagi apakah sudah benar?
class NumberOfAccessedVariables(private val config: Config?) : DetektVisitor() {

    val theMap = HashMap<String, Int>()

    var variablesCount: Int = 0
        private set

    private fun inc(){
        variablesCount++
    }

    private fun dec(){
        variablesCount--
    }

    // mungkin ini bisa untuk metric NOAV?
    override fun visitSimpleNameExpression(expression: KtSimpleNameExpression) {
        super.visitSimpleNameExpression(expression)
        val isVariable = (expression.getQualifiedElement().toString() == "REFERENCE_EXPRESSION" || expression.getQualifiedElement().toString() == "DOT_QUALIFIED_EXPRESSION")
        if (isVariable){
            theMap[expression.getReferencedName()] = 0
        }
//        println("${expression.getReferencedName()} : ${expression.getQualifiedElement()}")
    }

    // mungkin ini bisa untuk metric NOAV?
//    override fun visitExpression(expression: KtExpression) {
//        super.visitExpression(expression)
//        println("${expression.name} : ${expression.text}")
//    }




    companion object {
        fun calculate(node: KtElement): Int {
            val visitor = NumberOfAccessedVariables(null)
            node.accept(visitor)
//            println(visitor.theMap)
            return visitor.theMap.size
        }
    }
}
