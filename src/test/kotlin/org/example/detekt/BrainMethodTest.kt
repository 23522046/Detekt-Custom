package org.example.detekt

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Config
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
            @SuppressLint("DrawAllocation")
            override fun onDraw(canvas: Canvas) {
                val intensity: Float = delegate?.getIntensity() ?: 0f
                //imageReceiver.setAlpha(Math.abs(intensity))
                imageReceiver.setBlendMode(null)

                val backgroundColor: Int = delegate?.getBackgroundColor() ?: 0
                val backgroundGradientColor1: Int = delegate?.getBackgroundGradientColor1() ?: 0
                val backgroundGradientColor2: Int = delegate?.getBackgroundGradientColor2() ?: 0
                val backgroundGradientColor3: Int = delegate?.getBackgroundGradientColor3() ?: 0
                val backgroundGradientAngle: Int = delegate?.getBackgroundGradientAngle() ?: 0
                val checkColor: Int = delegate?.getCheckColor() ?: 0

                if (backgroundGradientColor1 != 0) {
                    if (gradientShader == null || backgroundColor != currentBackgroundColor || backgroundGradientColor1 != currentGradientColor1 || backgroundGradientColor2 != currentGradientColor2 || backgroundGradientColor3 != currentGradientColor3 || backgroundGradientAngle != currentGradientAngle) {
                        currentBackgroundColor = backgroundColor
                        currentGradientColor1 = backgroundGradientColor1
                        currentGradientColor2 = backgroundGradientColor2
                        currentGradientColor3 = backgroundGradientColor3
                        currentGradientAngle = backgroundGradientAngle

                        if (backgroundGradientColor2 != 0) {
                            gradientShader = null
                            if (backgroundDrawable != null) {
                                backgroundDrawable?.setColors(
                                    backgroundColor,
                                    backgroundGradientColor1,
                                    backgroundGradientColor2,
                                    backgroundGradientColor3,
                                    0,
                                    false
                                )
                            } else {
                                backgroundDrawable = MotionBackgroundDrawable(
                                    backgroundColor,
                                    backgroundGradientColor1,
                                    backgroundGradientColor2,
                                    backgroundGradientColor3,
                                    true
                                )
                                backgroundDrawable?.setRoundRadius(AndroidUtilities.dp(6))
                                backgroundDrawable?.setParentView(this)
                            }
                            if (intensity < 0) {
                                imageReceiver.setGradientBitmap(backgroundDrawable?.getBitmap())
                            } else {
                                imageReceiver.setGradientBitmap(null)
                                if (Build.VERSION.SDK_INT >= 29) {
                                    imageReceiver.setBlendMode(BlendMode.SOFT_LIGHT)
                                } else {
                                    imageReceiver.setColorFilter(PorterDuffColorFilter(delegate?.getPatternColor() ?: 0, PorterDuff.Mode.SRC_IN))
                                }
                            }
                        } else {
                            val r: Rect = BackgroundGradientDrawable.getGradientPoints(
                                currentGradientAngle,
                                measuredWidth,
                                measuredHeight
                            )
                            gradientShader = LinearGradient(
                                r.left.toFloat(),
                                r.top.toFloat(),
                                r.right.toFloat(),
                                r.bottom.toFloat(),
                                intArrayOf(backgroundColor, backgroundGradientColor1),
                                null,
                                Shader.TileMode.CLAMP
                            )
                            backgroundDrawable = null
                            imageReceiver.setGradientBitmap(null)
                        }
                    }
                } else {
                    gradientShader = null
                    backgroundDrawable = null
                    imageReceiver.setGradientBitmap(null)
                }
                if (backgroundDrawable != null) {
                    backgroundDrawable?.setBounds(0, 0, measuredWidth, measuredHeight)
                    backgroundDrawable?.draw(canvas)
                } else {
                    backgroundPaint.shader = gradientShader
                    if (gradientShader == null) {
                        backgroundPaint.color = backgroundColor
                    }
                    rect.set(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
                    canvas.drawRoundRect(rect, AndroidUtilities.dp(6).toFloat(), AndroidUtilities.dp(6).toFloat(), backgroundPaint)
                }

                super.onDraw(canvas)

                if (radialProgress.getIcon() != MediaActionDrawable.ICON_NONE) {
                    radialProgress.setColors(checkColor, checkColor, 0xffffffff.toInt(), 0xffffffff.toInt())
                    radialProgress.draw(canvas)
                }
            }
        """

    @Test
    fun `should expect brain method`(){
        val ktFile = compileContentForTest(codeOld)
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

        val brainMethod = BrainMethod(Config.empty)

        assert(brainMethod.isDetected(LOC, CYCLO, MAXNESTING, NOAV))

//        assert(ktFile.getUserData(MetricProcessor.numberOfLineOfCode)?.equals(18) ?: false)
    }
}
