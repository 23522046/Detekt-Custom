package org.example.detekt.metrics

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.rules.identifierName
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedElement
import org.jetbrains.kotlin.psi.psiUtil.isFunctionalExpression

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

        // dapatkan semua atribut class dan type referencenya
        val attributeMap = klass.declarations.filterIsInstance<KtProperty>().map { Pair(it.identifierName(), it.typeReference?.text?.replace("?", "")) }.toSet()
//        println("attributeMap : $attributeMap")

        val constructorMap = mutableSetOf<Pair<String, String?>>()
        if (klass.primaryConstructor!=null){
            constructorMap.addAll(klass.primaryConstructor!!.valueParameters.map { Pair(it.identifierName(), it.typeReference?.text?.replace("?", "")) }.toSet())
//            println("constructorMap : $constructorMap")
        }
//        println()


        val visitor = object : DetektVisitor() {
            override fun visitNamedFunction(function: KtNamedFunction) {
                super.visitNamedFunction(function)

                // dapatkan semua parameter function dan type referencenya
                val parametersMap = mutableSetOf<Pair<String, String?>>()
                if (function.valueParameterList?.parameters!=null){
                    parametersMap.addAll(function.valueParameterList?.parameters!!.map { Pair(it.identifierName(), it.typeReference?.text?.replace("?", "")) }.toSet())
                }
//                println("${function.name}() : $parametersMap")

                val visitor = object : DetektVisitor(){
                    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
                        super.visitDotQualifiedExpression(expression)

                        val expressionName = expression.text.split(".").first()
                        // cek apakah expressionName berasal dari attributeMap atau constructorMap, jika iya tampung
                        val isBerasal = (attributeMap.any { it.first == expressionName } || constructorMap.any { it.first == expressionName } || parametersMap.any { it.first == expressionName })
                        if (attributeMap.any { it.first == expressionName && it.second != null }){
                            foreignDataAccesses.add(attributeMap.filter { it.first == expressionName }.first().second!!)
                        }

                        if (constructorMap.any { it.first == expressionName && it.second != null}){
                            foreignDataAccesses.add(constructorMap.filter { it.first == expressionName }.first().second!!)
                        }

                        if (parametersMap.any { it.first == expressionName && it.second != null}){
                            foreignDataAccesses.add(parametersMap.filter { it.first == expressionName }.first().second!!)
                        }

//                        println("apakah $expressionName berasal dari attributeMap atau constructorMap atau mapParameters? : $isBerasal")


//                        println(expressionName)
//                        println("=======================")
//                        foreignDataAccesses.add(expressionName)
                    }
                }

                function.accept(visitor)
//                println("\\ end of method")
//                println()
            }

            override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
                super.visitDotQualifiedExpression(expression)

//                println(expression.text.split(".").first())
//                println("=======================")
//                foreignDataAccesses.add(expression.text)
            }
        }

        klass.accept(visitor)
        println("foreignDataAccesses : $foreignDataAccesses")
        return foreignDataAccesses.size
    }

    companion object {
        fun calculate(node: KtElement): Int{
            val visitor = AccessToForeignData(null)
            node.accept(visitor)
            return visitor.attributesCount
        }
    }
}
