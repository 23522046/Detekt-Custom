package org.example.detekt

import io.gitlab.arturbosch.detekt.api.FileProcessListener
import org.example.detekt.metrics.*
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile

class MetricProcessor : FileProcessListener {

    // for brain method
//    override fun onProcess(file: KtFile) {
//        file.putUserData(numberOfCyclomaticComplexity, CyclomaticComplexity.calculate(file))
//        file.putUserData(numberOfLineOfCode, file.linesOfCode())
//        file.putUserData(numberOfNestedDepth, MaximumNesting.calculate(file))
//        file.putUserData(numberOfAccessedVariables, NumberOfAccessedVariables.calculate(file))
//    }


    // for god class
//    override fun onProcess(file: KtFile) {
//        file.putUserData(numberOfAccessToForeignData, AccessToForeignData.calculate(file))
//        file.putUserData(numberOfWeightedMethodCount, WeightedMethodCount.calculate(file))
//        file.putUserData(numberOfTightClassCohesion, TightClassCohesion.calculate(file))
//    }

    // for brain class
    override fun onProcess(file: KtFile) {
        file.putUserData(numberOfBrainMethofCount, BrainMethodCount.calculate(file))
        file.putUserData(numberOfLineOfCode, file.linesOfCode())
        file.putUserData(numberOfWeightedMethodCount, WeightedMethodCount.calculate(file))
        file.putUserData(numberOfTightClassCohesion, TightClassCohesion.calculate(file))
    }

    companion object {

        val numberOfCyclomaticComplexity = Key<Int>("number of complexity")
        val numberOfNestedDepth = Key<Int>("number of nested depth")
        val numberOfLineOfCode = Key<Int>("number of line of code")
        val numberOfAccessedVariables = Key<Int>("number of accessed variable")

        val numberOfAccessToForeignData = Key<Int>("number of access to foreign data")
        val numberOfWeightedMethodCount = Key<Int>("number of weighted method count")
        val numberOfTightClassCohesion = Key<Double>("number of tight class cohesion")

        val numberOfBrainMethofCount = Key<Int>("number of brain method count")
    }

}
