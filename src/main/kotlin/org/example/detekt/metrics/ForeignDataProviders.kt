package org.example.detekt.metrics

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

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
    val listFDP = mutableSetOf<KtProperty>()

    var FDPCount = 0
        private set

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        FDPCount = countForeignDataProviders(function)
    }

    private fun countForeignDataProviders(function: KtNamedFunction): Int {
        val localAttributes = function.containingClass()?.declarations?.filterIsInstance<KtProperty>() ?: listOf()
//        println("localAttr : ${localAttributes?.map { "${it.name} : ${it.typeReference?.typeElement?.text}" }}")

        val foreignAttributeAccesses = mutableListOf<KtDotQualifiedExpression>()

        val visitor = object : DetektVisitor() {
            // Still in development
//            fun findVariableDeclaration(expression: KtReferenceExpression): KtProperty? {
//                var current = expression.parent
//                while (current != null && current !is KtFile) {
//                    if (current is KtProperty) {
//                        return current
//                    }
//                    current = current.parent
//                }
//                return null
//            }

            override fun visitReferenceExpression(expression: KtReferenceExpression) {
                super.visitReferenceExpression(expression)
//                println("${expression.text}\t\t\t: ${expression}")
                if (expression.toString()=="REFERENCE_EXPRESSION"){
//                    println("${expression.text} : ${localAttributes?.any { it.name == expression.text }}")
                    val prop = localAttributes.find { it.typeReference!=null && (it.name == expression.text) }
                    // jika menggunakan public property milik class
                    if (prop != null) {
                        listFDP.add(prop)
                    } else {
                        /*
                        // Still in development
                        val variableDeclaration = findVariableDeclaration(expression)
                        if (variableDeclaration != null) {
                            val initializer = variableDeclaration.initializer
                            if (initializer != null) {
                                println("Variable ${expression.text} is initialized with: ${initializer.text}")
                            }
                        }
                         */
//                        println("${expression.text}\t\t: ")
                    }

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
//        println(foreignAttributeAccesses.map { "${it.text.split('.').first()}" }.toSet())
        // TODO: coba temukan class pemilik object tersebut untuk dihitung nilai FDP nya

//        println("method : ${function.name}; menggunakan class :  ${listFDP.map { "${it.name} : ${it.typeReference?.typeElement?.text}" }}}")
        return listFDP.size
    }

    companion object {
        fun calculate(node: KtElement): Int{
            val visitor = ForeignDataProviders(null)
            node.accept(visitor)
            return visitor.FDPCount
        }
    }
}
