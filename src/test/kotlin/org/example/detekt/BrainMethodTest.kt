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
        fun drawFrame(st: SurfaceTexture) {
        var blurred = false
        if (isPhoto) {
            drawGradient()
        } else {
            st.getTransformMatrix(mSTMatrix)
            if (BuildVars.LOGS_ENABLED && firstFrame) {
                val builder = StringBuilder()
                for (a in mSTMatrix.indices) {
                    builder.append(mSTMatrix[a]).append(", ")
                }
                FileLog.d("stMatrix = "+builder)
                firstFrame = false
            }
            if (blendEnabled) {
                GLES20.glDisable(GLES20.GL_BLEND)
                blendEnabled = false
            }
            var texture: Int
            var target: Int
            var index: Int
            var stMatrix: FloatArray
            if (filterShaders != null) {
                filterShaders.onVideoFrameUpdate(mSTMatrix)
                GLES20.glViewport(0, 0, originalWidth, originalHeight)
                filterShaders.drawSkinSmoothPass()
                filterShaders.drawEnhancePass()
                filterShaders.drawSharpenPass()
                filterShaders.drawCustomParamsPass()
                blurred = filterShaders.drawBlurPass()
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
                if (transformedWidth != originalWidth || transformedHeight != originalHeight) {
                    GLES20.glViewport(0, 0, transformedWidth, transformedHeight)
                }
                texture = filterShaders.getRenderTexture(if (blurred) 0 else 1)
                index = NUM_FILTER_SHADER
                target = GLES20.GL_TEXTURE_2D
                stMatrix = mSTMatrixIdentity
            } else {
                texture = mTextureID
                index = NUM_EXTERNAL_SHADER
                target = GLES11Ext.GL_TEXTURE_EXTERNAL_OES
                stMatrix = mSTMatrix
            }
            drawGradient()
            GLES20.glUseProgram(mProgram[index])
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(target, texture)
            GLES20.glVertexAttribPointer(maPositionHandle[index], 2, GLES20.GL_FLOAT, false, 8, verticesBuffer)
            GLES20.glEnableVertexAttribArray(maPositionHandle[index])
            GLES20.glVertexAttribPointer(maTextureHandle[index], 2, GLES20.GL_FLOAT, false, 8, renderTextureBuffer)
            GLES20.glEnableVertexAttribArray(maTextureHandle[index])
            if (texSizeHandle != 0) {
                GLES20.glUniform2f(texSizeHandle, transformedWidth, transformedHeight)
            }
            GLES20.glUniformMatrix4fv(muSTMatrixHandle[index], 1, false, stMatrix, 0)
            GLES20.glUniformMatrix4fv(muMVPMatrixHandle[index], 1, false, mMVPMatrix, 0)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        }
        if (blur != null) {
            if (!blendEnabled) {
                GLES20.glEnable(GLES20.GL_BLEND)
                GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
                blendEnabled = true
            }
            var tex = -1
            var w = 1
            var h = 1
            if (imagePath != null && paintTexture != null) {
                tex = paintTexture[0]
                w = imageWidth
                h = imageHeight
            } else if (filterShaders != null) {
                tex = filterShaders.getRenderTexture(if (blurred) 0 else 1)
                w = filterShaders.getRenderBufferWidth()
                h = filterShaders.getRenderBufferHeight()
            }
            if (tex != -1) {
                blur.draw(null, tex, w, h)
                GLES20.glViewport(0, 0, transformedWidth, transformedHeight)
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
                GLES20.glUseProgram(blurShaderProgram)
                GLES20.glEnableVertexAttribArray(blurInputTexCoordHandle)
                GLES20.glVertexAttribPointer(blurInputTexCoordHandle, 2, GLES20.GL_FLOAT, false, 8, gradientTextureBuffer)
                GLES20.glEnableVertexAttribArray(blurPositionHandle)
                GLES20.glVertexAttribPointer(blurPositionHandle, 2, GLES20.GL_FLOAT, false, 8, blurVerticesBuffer)
                GLES20.glUniform1i(blurBlurImageHandle, 0)
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, blur.getTexture())
                GLES20.glUniform1i(blurMaskImageHandle, 1)
                GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, blurTexture[0])
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
            }
        }
        if (isPhoto || paintTexture != null || stickerTexture != null || partsTexture != null) {
            GLES20.glUseProgram(simpleShaderProgram)
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)

            GLES20.glUniform1i(simpleSourceImageHandle, 0)
            GLES20.glEnableVertexAttribArray(simpleInputTexCoordHandle)
            GLES20.glVertexAttribPointer(simpleInputTexCoordHandle, 2, GLES20.GL_FLOAT, false, 8, textureBuffer)
            GLES20.glEnableVertexAttribArray(simplePositionHandle)
        }
        if (paintTexture != null && imagePath != null) {
            for (a in 0 until 1) {
                drawTexture(true, paintTexture[a], -10000f, -10000f, -10000f, -10000f, 0f, false, useMatrixForImagePath && isPhoto && a == 0, -1)
            }
        }
        if (partsTexture != null) {
            for (a in partsTexture.indices) {
                drawTexture(true, partsTexture[a], -10000f, -10000f, -10000f, -10000f, 0f, false, false, a)
            }
        }
        if (paintTexture != null) {
            for (a in if (imagePath != null) 1 else 0 until paintTexture.size) {
                drawTexture(true, paintTexture[a], -10000f, -10000f, -10000f, -10000f, 0f, false, useMatrixForImagePath && isPhoto && a == 0, -1)
            }
        }
        if (stickerTexture != null) {
            for (a in mediaEntities.indices) {
                drawEntity(mediaEntities[a], mediaEntities[a].color)
            }
        }
        GLES20.glFinish()
    }
    }
    """.trimIndent()

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
