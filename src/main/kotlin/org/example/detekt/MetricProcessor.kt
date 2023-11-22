package org.example.detekt

import io.gitlab.arturbosch.detekt.api.FileProcessListener
import org.example.detekt.metrics.*
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile

class MetricProcessor : FileProcessListener {

    // for brain method
    override fun onProcess(file: KtFile) {
        file.putUserData(numberOfCyclomaticComplexity, CyclomaticComplexity.calculate(file))
        file.putUserData(numberOfLineOfCode, file.linesOfCode())
        file.putUserData(numberOfNestedDepth, MaximumNesting.calculate(file))
        file.putUserData(numberOfAccessedVariables, NumberOfAccessedVariables.calculate(file))
    }


    // for god class
//    override fun onProcess(file: KtFile) {
//        file.putUserData(numberOfAccessToForeignData, AccessToForeignData.calculate(file))
//        file.putUserData(numberOfWeightedMethodCount, WeightedMethodCount.calculate(file))
//        file.putUserData(numberOfTightClassCohesion, TightClassCohesion.calculate(file))
//    }

    // for brain class
//    override fun onProcess(file: KtFile) {
//        file.putUserData(numberOfBrainMethodCount, BrainMethodCount.calculate(file))
//        file.putUserData(numberOfLineOfCode, file.linesOfCode())
//        file.putUserData(numberOfWeightedMethodCount, WeightedMethodCount.calculate(file))
//        file.putUserData(numberOfTightClassCohesion, TightClassCohesion.calculate(file))
//    }

    // for feature envy
//    override fun onProcess(file: KtFile) {
//        file.putUserData(numberOfLocalityOfAttributeAccesses, LocalityOfAttributeAccesses.calculate(file))
//        file.putUserData(numberOfAccessToForeignData, AccessToForeignData.calculate(file))
//        file.putUserData(numberOfForeignDataProviders, ForeignDataProviders.calculate(file))
//    }

    // for data class
//    override fun onProcess(file: KtFile) {
//        file.putUserData(numberOfPublicAttribute, NumberOfPublicAttribute.calculate(file))
//        file.putUserData(numberOfAccessorMethod, NumberOfAccessorMethod.calculate(file))
//        file.putUserData(numberOfWeightOfClass, WeightOfClass.calculate(file))
//        file.putUserData(numberOfWeightedMethodCount, WeightedMethodCount.calculate(file))
//    }

    companion object {

        val numberOfCyclomaticComplexity = Key<Int>("number of complexity")
        val numberOfNestedDepth = Key<Int>("number of nested depth")
        val numberOfLineOfCode = Key<Int>("number of line of code")
        val numberOfAccessedVariables = Key<Int>("number of accessed variable")

        val numberOfAccessToForeignData = Key<Int>("number of access to foreign data")
        val numberOfAccessToForeignDataDev = Key<Int>("number of access to foreign data dev")
        val numberOfWeightedMethodCount = Key<Int>("number of weighted method count")
        val numberOfTightClassCohesion = Key<Double>("number of tight class cohesion")

        val numberOfBrainMethodCount = Key<Int>("number of brain method count")
        val numberOfLocalityOfAttributeAccesses = Key<Double>("number of locality of attribute access")
        val numberOfForeignDataProviders = Key<Int>("number of foreign data providers")

        val numberOfPublicAttribute = Key<Int>("number of public attributes")
        val numberOfAccessorMethod = Key<Int>("number of accessor method")
        val numberOfWeightOfClass = Key<Double>("number of weight of class")
    }

}
