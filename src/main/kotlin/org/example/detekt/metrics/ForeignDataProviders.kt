package org.example.detekt.metrics

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.*

/**
    The Foreign Data Providers (FDP) is the total number of external components (or classes)
    from which foreign attributes are accessed.
    Here, these external classes are provisioning data.
    Ideally, all the data should be locally available within the class to promote cohesion.

    Ex :
    Consider method ‘M’ in class ‘A’ is accessing attributes from classes 'B', 'C', and 'D'.
    Thus, FDP for the method ‘M’ = 3
 */

class ForeignDataProviders(val config: Config?) : DetektVisitor(){
    var FDPCount = 0
        private set

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        FDPCount = countForeignDataProviders(function)
    }

    private fun countForeignDataProviders(function: KtNamedFunction): Int {
        val localAttributes = (function.parent as KtClassBody).properties

        val foreignAttributeAccesses = mutableListOf<KtDotQualifiedExpression>()

        val visitor = object : DetektVisitor() {
            override fun visitReferenceExpression(expression: KtReferenceExpression) {
                super.visitReferenceExpression(expression)
//                println("${expression.text}\t\t\t: ${expression}")
                if (expression.toString()=="REFERENCE_EXPRESSION"){

//                    println("${expression.text} : ${localAttributes.any { it.name == expression.text }}")
//                    if (localAttributes.any { it.name == expression.text }){
//                        foreignAttributeAccesses.add(expression.text)
//                    }
                }
            }

            override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
                super.visitDotQualifiedExpression(expression)
                foreignAttributeAccesses.add(expression)
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
        println(foreignAttributeAccesses.map { "${it.text} : ${it.parent.text}" })
        // TODO: coba temukan class pemilik object tersebut untuk dihitung nilai FDP nya

        println("method : ${function.name} : ")
        return 0
    }

    companion object {
        fun calculate(node: KtElement): Int{
            val visitor = ForeignDataProviders(null)
            node.accept(visitor)
            return visitor.FDPCount
        }
    }
}
