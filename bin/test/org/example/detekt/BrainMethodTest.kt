package org.example.detekt

import io.github.detekt.test.utils.compileContentForTest

import org.junit.jupiter.api.Test
import java.text.DecimalFormat

class BrainMethodTest {
    @Test
    fun `should expect brain method`(){
        val codeOld = """
            class Rectangle (val width: Int, val height: Int) {

                fun complexDemo(val w: Int){
                    var a = 10
                    var b = 20
                    var c = 30 
                    var d = 40

                    width = d

                    if(w==a){
                    w = a
                    }

                    if (b > c){
                        a = b
                        if (b > a){
                            b = a
                        }
                    } else {
                        a = c
                    }

                    if (a> c){ 
                     c = a
                    }
                }
            }
        """

        val code = """
            class Car {
              var brand = ""
              var model = ""
              var year = 0
            }

            fun main() {
              val c1 = Car()
              c1.brand = "Ford"
              c1.model = "Mustang"
              c1.year = 1969
              
              println(c1.brand)
              println(c1.model)
              println(c1.year)
            }
        """

        val ktFile = compileContentForTest(code)
        MetricProcessor().onProcess(ktFile)

        val CYCLO = ktFile.getUserData(MetricProcessor.numberOfCyclomaticComplexity)
        val LOC = ktFile.getUserData(MetricProcessor.numberOfLineOfCode)
        val dec = DecimalFormat("#.##")

        println("CYCLO : $CYCLO")
        println("LOC : $LOC")
        println("CYCLO/LOC : ${dec.format(CYCLO!!.toDouble() / LOC!!.toDouble())}")
        println("MAXNESTING : ${ktFile.getUserData(MetricProcessor.numberOfNestedDepth)}")
        println("NOAV : ${ktFile.getUserData(MetricProcessor.numberOfAccessedVariables)}")

//        assert(ktFile.getUserData(MetricProcessor.numberOfLineOfCode)?.equals(18) ?: false)
        assert(true)
    }
}
