package org.example.detekt.metrics

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedElement
import org.jetbrains.kotlin.psi.psiUtil.isFunctionalExpression

// TODO: cek lagi apakah metric ATFD ini hanya diaplikasikan ke class atau juga bisa single method/function ?
class AccessToForeignData(private val config: Config?) : DetektVisitor()  {
    var attributesCount: Int = 0
        private set

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        attributesCount = countAccessToForeignData(klass)
    }



    private fun countAccessToForeignData(klass: KtClass): Int {
//        println("\nCounting for class : ${klass.name}\n=============================")
        val foreignDataAccesses = mutableSetOf<String>()

        val visitor = object : DetektVisitor(){
            override fun visitElement(element: PsiElement) {
                super.visitElement(element)
                if (element.toString() == "DOT_QUALIFIED_EXPRESSION"){
                    foreignDataAccesses.add(element.text)
                }
//                if (element.toString()!="PsiWhiteSpace"){
                    //                println("${element.text}, PSiElement(IDENTIFIER) : ${element.equals("REFERENCE_EXPRESSION")}")
//                if (element.toString()=="REFERENCE_EXPRESSION"){
//                    println("${element.text}\t\t\t\t: ${element}")
//                }

//                }
            }
        }

        klass.accept(visitor)
//        println("ATFD : ${foreignDataAccesses.size}")
//        foreignDataAccesses.forEach { println(it) }
        return  foreignDataAccesses.size
    }

    companion object {
        fun calculate(node: KtElement): Int{
            val visitor = AccessToForeignData(null)
            node.accept(visitor)
            return visitor.attributesCount
        }
    }
}
