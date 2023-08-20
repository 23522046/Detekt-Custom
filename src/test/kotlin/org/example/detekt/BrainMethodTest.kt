package org.example.detekt

import io.github.detekt.test.utils.compileContentForTest
import org.example.detekt.smells.BrainMethod

import org.junit.jupiter.api.Test
import java.text.DecimalFormat

class BrainMethodTest {
    val code = """
            fun complexDemo(val w: Int, val h: Int, val i: Int){
                    var a = 10
                    var b = 20
                    var c = 30 
                    var d = 40

                    h++
                    i++

                    length = i

                    width = d

                    if(w==a){
                    w = a
                    }

                    if (b > c){
                        a = b
                        if (b > a){
                            b = a
                            if (a!=0){
                                if (b!=0){
                                    
                                }
                            } else if (c!=0){
                                if (d!=0){
                                    // here
                                    if (a!=b){
                                        while (a<b){
                                            if (b>100){
                                            
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        a = c
                    }

                    if (b > c){
                        a = b
                        if (b > a){
                            b = a
                            if (a!=0){
                                if (b!=0){
                                    
                                }
                            } else if (c!=0){
                                if (d!=0){
                                    // here
                                    if (a!=b){
                                        while (a<b){
                                            if (b>100){
                                            
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        a = c
                    }

                    if (b > c){
                        a = b
                        if (b > a){
                            b = a
                            if (a!=0){
                                if (b!=0){
                                    
                                }
                            } else if (c!=0){
                                if (d!=0){
                                    // here
                                    if (a!=b){
                                        while (a<b){
                                            if (b>100){
                                            
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        a = c
                    }

                    if (a> c){ 
                     c = a
                    }
                }
        """

    val codeOld = """
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

    @Test
    fun `should expect brain method`(){
        val ktFile = compileContentForTest(code)
        MetricProcessor().onProcess(ktFile)

        val CYCLO = ktFile.getUserData(MetricProcessor.numberOfCyclomaticComplexity) ?: -1
        val LOC = ktFile.getUserData(MetricProcessor.numberOfLineOfCode) ?: -1
        val dec = DecimalFormat("#.##")
        val MAXNESTING = ktFile.getUserData(MetricProcessor.numberOfNestedDepth) ?: -1
        val NOAV = ktFile.getUserData(MetricProcessor.numberOfAccessedVariables) ?: -1

        println("CYCLO : $CYCLO")
        println("LOC : $LOC")
        println("CYCLO/LOC : ${dec.format(CYCLO.toDouble() / LOC.toDouble())}")
        println("MAXNESTING : $MAXNESTING")
        println("NOAV : $NOAV")

        assert(BrainMethod.isDetected(LOC, CYCLO, MAXNESTING, NOAV))

//        assert(ktFile.getUserData(MetricProcessor.numberOfLineOfCode)?.equals(18) ?: false)
    }
}
