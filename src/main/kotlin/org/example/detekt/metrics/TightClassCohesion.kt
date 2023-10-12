package org.example.detekt.metrics

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

/**
 * Insyaallah fix, silahkan ditest
 */
class TightClassCohesion(private val config: Config?) : DetektVisitor() {
    var tccCount: Double = 0.0
        private set

    override fun visitClass(klass: KtClass) {
        val methodCount = klass.declarations.count { it is KtNamedFunction }

        var directConnections = 0
        val arr = mutableListOf<MutableList<String>>()

        // add attribute from declaration
        val attributes = klass.declarations.filterIsInstance<KtProperty>().map { it.name }.toMutableList()
        // add attribute from param constructor
        if (klass.primaryConstructor!=null){
            attributes.addAll(klass.primaryConstructor!!.valueParameters.map { it.name }.toList())
        }

//        println(klass.declarations.filterIsInstance<KtNamedFunction>().map { it.name })

        for (method in klass.declarations.filterIsInstance<KtNamedFunction>()) {
            val methodReferences = method.collectDescendantsOfType<KtReferenceExpression>()
            val arrAttribute = mutableListOf<String>()
            for (attribute in attributes){
                // jika atribut adalah public attribut dan bukan merupakan parameter method
                if (!method.valueParameters.any { it.name == attribute }){
                    if (methodReferences.any { it.text == attribute }){
                        arrAttribute.add(attribute!!)
                    }
                }
            }
            arr.add(arrAttribute)

        }

        // for debug
//        arr.forEachIndexed { index, a ->
//            print("method ke-$index : ")
//            print(a.map { it })
//            println()
//        }

        val listI = arr
        val listJ = listI.toMutableList()
        for (i in listI){
            listJ.removeFirst()
            for (j in listJ){
                // for debug
                // println("apakah ${i} intersect dengan ${j} : ${i.intersect(j).isNotEmpty()}")
                if (i.intersect(j).isNotEmpty()){
                    directConnections++
                }
            }
        }

        val np = methodCount * (methodCount - 1) / 2

//        println("directConnections : $directConnections")
//        println("NP : $np")
        tccCount = if (np > 0) directConnections.toDouble() / np.toDouble() else 0.0
    }

    companion object {
        fun calculate(node: KtElement): Double{
            val visitor = TightClassCohesion(null)
            node.accept(visitor)
            return visitor.tccCount
        }
    }
}
