package org.example.detekt.metrics

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.*

/**
    The Locality of Attribute Access (LAA) is the ratio of the number of attributes accessed
    from a method’s (or function’s) owner class
    to the total number of attributes accessed in the method.
    This is a subcomponent (method) level metric.

    Ex :
    Consider method ‘M’ in class ‘A’ is accessing 2 attributes from class ‘A’.
    Method ‘M’ is also accessing 1 attribute from foreign class ‘B’ and 3 attributes from another foreign class ‘C’.
    Thus, LAA for the method ‘M’  = 2/ (2+1+3) = 2/6 = 0.33
 */

// TODO: class ini sedang dalam pengembangan, untuk smell Feature Envy
class LocalityOfAttributeAccesses(val config: Config?) : DetektVisitor(){
    var LAACount = 0.0
        private set

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        LAACount = countLocalityOfAttributeAccesses(function)
    }

    private fun countLocalityOfAttributeAccesses(function: KtNamedFunction): Double {
        val localAttributes = if (function.parent is KtClassBody) (function.parent as KtClassBody).properties else listOf()
        val attributeAccesses = mutableSetOf<String>()
        val foreignAttributeAccesses = mutableSetOf<String>()
//        println(localAttributes.map { it.name })

        val visitor = object : DetektVisitor() {
            override fun visitReferenceExpression(expression: KtReferenceExpression) {
                super.visitReferenceExpression(expression)
//                println("${expression.text}\t\t\t: ${expression}")
                if (expression.toString()=="REFERENCE_EXPRESSION"){

//                    println("${expression.text} : ${localAttributes.any { it.name == expression.text }}")
                    if (localAttributes.any { it.name == expression.text }){
                        attributeAccesses.add(expression.text)
                    }
                }
            }

            override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
                super.visitDotQualifiedExpression(expression)
                foreignAttributeAccesses.add(expression.text)
            }

            override fun visitElement(element: PsiElement) {
                super.visitElement(element)
//                println(element)
                if (element.toString() != "PsiWhiteSpace") {
//                    println("${element.text}, PSiElement(IDENTIFIER) : ${element.equals("REFERENCE_EXPRESSION")}")
//                    if (element.toString() == "REFERENCE_EXPRESSION") {

//                        println("${element.text}\t\t\t\t: ${element}\t: ${element}")
//                    }
                }
            }
        }
        function.accept(visitor)

//        println(localAttributes.map { it.name });
//        println(attributeAccesses.size)
//        println(foreignAttributeAccesses.size)
        val LAA = attributeAccesses.size.toDouble() / (attributeAccesses.size.toDouble() + foreignAttributeAccesses.size.toDouble())
//        println("method : ${function.name} : $LAA")
        return LAA
    }

    companion object {
        fun calculate(node: KtElement): Double{
            val visitor = LocalityOfAttributeAccesses(null)
            node.accept(visitor)
            return visitor.LAACount
        }
    }
}
