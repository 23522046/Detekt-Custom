package org.example.detekt

import io.gitlab.arturbosch.detekt.api.FileProcessListener
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile

class NumberOfComplexityProcessor : FileProcessListener {

    override fun onProcess(file: KtFile) {
        val visitor = CyclomaticComplexity(CyclomaticComplexity.Config())
        file.accept(visitor)
        file.putUserData(numberOfCyclomaticComplexity, visitor.complexity)
    }

    companion object {
        val numberOfCyclomaticComplexity = Key<Int>("number of complexity")

    }

}
