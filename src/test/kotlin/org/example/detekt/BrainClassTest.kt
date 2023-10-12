package org.example.detekt

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Config
import org.example.detekt.smells.BrainClass
import org.junit.jupiter.api.Test
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class BrainClassTest {
    val listOfUrlClassTest = listOf(
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/ui/QuranActivity.kt",
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/ui/fragment/BookmarksFragment.java",
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/ui/fragment/QuranPageFragment.java",
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/ui/translation/TranslationView.java",
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/database/DatabaseHandler.kt",
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/service/AudioService.kt",
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/service/QuranDownloadService.java"
    )

    val code = """
        package org.telegram.messenger.video

        import android.graphics.Bitmap
        import android.graphics.BitmapFactory
        import android.graphics.Canvas
        import android.graphics.Paint
        import android.opengl.GLES11Ext
        import android.opengl.GLES20
        import android.opengl.GLES30
        import android.opengl.GLUtils
        import android.opengl.Matrix
        import android.os.Build
        import android.text.Layout
        import android.text.SpannableString
        import android.text.Spanned
        import android.text.style.ReplacementSpan
        import android.util.Log
        import android.util.Pair
        import android.util.TypedValue
        import android.view.Gravity
        import android.view.View
        import android.view.inputmethod.EditorInfo
        import androidx.annotation.NonNull
        import androidx.annotation.Nullable
        import androidx.annotation.RequiresApi
        import androidx.exifinterface.media.ExifInterface
        import com.google.zxing.common.detector.MathUtils
        import org.telegram.messenger.AndroidUtilities
        import org.telegram.messenger.ApplicationLoader
        import org.telegram.messenger.Bitmaps
        import org.telegram.messenger.BuildVars
        import org.telegram.messenger.Emoji
        import org.telegram.messenger.FileLog
        import org.telegram.messenger.LocaleController
        import org.telegram.messenger.MediaController
        import org.telegram.messenger.MessageObject
        import org.telegram.messenger.UserConfig
        import org.telegram.messenger.Utilities
        import org.telegram.messenger.VideoEditedInfo
        import org.telegram.tgnet.TLRPC
        import org.telegram.ui.Components.AnimatedEmojiDrawable
        import org.telegram.ui.Components.AnimatedEmojiSpan
        import org.telegram.ui.Components.AnimatedFileDrawable
        import org.telegram.ui.Components.BlurringShader
        import org.telegram.ui.Components.EditTextEffects
        import org.telegram.ui.Components.FilterShaders
        import org.telegram.ui.Components.Paint.Views.EditTextOutline
        import org.telegram.ui.Components.Paint.Views.LocationMarker
        import org.telegram.ui.Components.Paint.Views.PaintTextOptionsView
        import org.telegram.ui.Components.RLottieDrawable
        import org.telegram.ui.Components.Rect
        import org.telegram.ui.Stories.recorder.StoryEntry
        import java.io.File
        import java.io.RandomAccessFile
        import java.nio.ByteBuffer
        import java.nio.ByteOrder
        import java.nio.FloatBuffer
        import java.nio.channels.FileChannel
        import java.util.ArrayList
        import javax.microedition.khronos.opengles.GL10

        class TextureRenderer {

            private var verticesBuffer: FloatBuffer
            private var gradientVerticesBuffer: FloatBuffer
            private var gradientTextureBuffer: FloatBuffer
            private var textureBuffer: FloatBuffer
            private var renderTextureBuffer: FloatBuffer
            private var bitmapVerticesBuffer: FloatBuffer

            private var blurVerticesBuffer: FloatBuffer

            private var partsVerticesBuffer: Array<FloatBuffer>?
            private var partsTextureBuffer: FloatBuffer
            private var parts: ArrayList<StoryEntry.Part>?
            private var partsTexture: IntArray?

            private var useMatrixForImagePath: Boolean

            private val bitmapData = floatArrayOf(
                -1.0f, 1.0f,
                1.0f, 1.0f,
                -1.0f, -1.0f,
                1.0f, -1.0f
            )

            private var filterShaders: FilterShaders? = null
            private var paintPath: String?
            private var blurPath: String?
            private var imagePath: String?
            private var imageWidth: Int
            private var imageHeight: Int
            private var mediaEntities: ArrayList<VideoEditedInfo.MediaEntity>?
            private var emojiDrawables: ArrayList<AnimatedEmojiDrawable>?
            private var originalWidth: Int
            private var originalHeight: Int
            private var transformedWidth: Int
            private var transformedHeight: Int

            private var blur: BlurringShader?

            private val VERTEX_SHADER =
                "uniform mat4 uMVPMatrix;\n" +
                        "uniform mat4 uSTMatrix;\n" +
                        "attribute vec4 aPosition;\n" +
                        "attribute vec4 aTextureCoord;\n" +
                        "varying vec2 vTextureCoord;\n" +
                        "void main() {\n" +
                        "  gl_Position = uMVPMatrix * aPosition;\n" +
                        "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                        "}\n"

            private val VERTEX_SHADER_300 =
                "#version 320 es\n" +
                        "uniform mat4 uMVPMatrix;\n" +
                        "uniform mat4 uSTMatrix;\n" +
                        "in vec4 aPosition;\n" +
                        "in vec4 aTextureCoord;\n" +
                        "out vec2 vTextureCoord;\n" +
                        "void main() {\n" +
                        "  gl_Position = uMVPMatrix * aPosition;\n" +
                        "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                        "}\n"

            private val FRAGMENT_EXTERNAL_SHADER =
                "#extension GL_OES_EGL_image_external : require\n" +
                        "precision highp float;\n" +
                        "varying vec2 vTextureCoord;\n" +
                        "uniform samplerExternalOES sTexture;\n" +
                        "void main() {\n" +
                        "  gl_FragColor = texture2D(sTexture, vTextureCoord);" +
                        "}\n"

            private val FRAGMENT_SHADER =
                "precision highp float;\n" +
                        "varying vec2 vTextureCoord;\n" +
                        "uniform sampler2D sTexture;\n" +
                        "void main() {\n" +
                        "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                        "}\n"

            private val GRADIENT_FRAGMENT_SHADER =
                "precision highp float;\n" +
                        "varying vec2 vTextureCoord;\n" +
                        "uniform vec4 gradientTopColor;\n" +
                        "uniform vec4 gradientBottomColor;\n" +
                        "float interleavedGradientNoise(vec2 n) {\n" +
                        "    return fract(52.9829189 * fract(.06711056 * n.x + .00583715 * n.y));\n" +
                        "}\n" +
                        "void main() {\n" +
                        "  gl_FragColor = mix(gradientTopColor, gradientBottomColor, vTextureCoord.y + (.2 * interleavedGradientNoise(gl_FragCoord.xy) - .1));\n" +
                        "}\n"

            private var NUM_FILTER_SHADER = -1
            private var NUM_EXTERNAL_SHADER = -1
            private var NUM_GRADIENT_SHADER = -1

            private val mMVPMatrix = FloatArray(16)
            private val mSTMatrix = FloatArray(16)
            private val mSTMatrixIdentity = FloatArray(16)
            private var mTextureID: Int
            private val mProgram: IntArray
            private val muMVPMatrixHandle: IntArray
            private val muSTMatrixHandle: IntArray
            private val maPositionHandle: IntArray
            private val maTextureHandle: IntArray
            private val gradientTopColorHandle: Int
            private val gradientBottomColorHandle: Int
            private val texSizeHandle: Int
            private val simpleShaderProgram: Int
            private val simplePositionHandle: Int
            private val simpleInputTexCoordHandle: Int
            private val simpleSourceImageHandle: Int
            private val blurShaderProgram: Int
            private val blurPositionHandle: Int
            private val blurInputTexCoordHandle: Int
            private val blurBlurImageHandle: Int
            private val blurMaskImageHandle: Int
            private val paintTexture: IntArray
            private val stickerTexture: IntArray
            private var stickerBitmap: Bitmap?
            private var stickerCanvas: Canvas?
            private val videoFps: Float
            private var imageOrientation: Int
            private var blendEnabled: Boolean
            private var isPhoto: Boolean
            private var firstFrame: Boolean = true
            private var path: Path?
            private var xRefPaint: Paint?
            private var textColorPaint: Paint?
            private val cropState: MediaController.CropState
            private val blurTexture: IntArray
            private var gradientTopColor: Int
            private var gradientBottomColor: Int

            init {
                isPhoto = photo
                this.parts = parts

                val texData = floatArrayOf(
                    0f, 0f,
                    1f, 0f,
                    0f, 1f,
                    1f, 1f
                )

                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("start textureRenderer w = " +w+ "h = "+h+" r = "+rotation+" fps = "+fps)
                    cropState?.let {
                        
                    }
                }

                textureBuffer = ByteBuffer.allocateDirect(texData.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
                textureBuffer.put(texData).position(0)

                bitmapVerticesBuffer = ByteBuffer.allocateDirect(bitmapData.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
                bitmapVerticesBuffer.put(bitmapData).position(0)

                Matrix.setIdentityM(mSTMatrix, 0)
                Matrix.setIdentityM(mSTMatrixIdentity, 0)

                savedFilterState?.let {
                    filterShaders = FilterShaders(true, hdrInfo)
                    filterShaders?.setDelegate(FilterShaders.getFilterShadersDelegate(it))
                }
                transformedWidth = w
                transformedHeight = h
                this.originalWidth = originalWidth
                this.originalHeight = originalHeight
                imagePath = image
                paintPath = paint
                blurPath = blurtex
                mediaEntities = entities
                videoFps = if (fps == 0f) 30f else fps
                this.cropState = cropState

                var count = 0
                NUM_EXTERNAL_SHADER = count++
                gradientTopColor?.let { _ ->
                    gradientBottomColor?.let { _ ->
                        NUM_GRADIENT_SHADER = count++
                    }
                }
                filterShaders?.let {
                    NUM_FILTER_SHADER = count++
                }
                mProgram = IntArray(count)
                muMVPMatrixHandle = IntArray(count)
                muSTMatrixHandle = IntArray(count)
                maPositionHandle = IntArray(count)
                maTextureHandle = IntArray(count)

                Matrix.setIdentityM(mMVPMatrix, 0)
                var textureRotation = 0
                gradientBottomColor?.let {
                    val verticesData = floatArrayOf(
                        -1.0f, -1.0f,
                        1.0f, -1.0f,
                        -1.0f, 1.0f,
                        1.0f, 1.0f
                    )
                    gradientVerticesBuffer =
                        ByteBuffer.allocateDirect(verticesData.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
                    gradientVerticesBuffer.put(verticesData).position(0)
                    val textureData = floatArrayOf(
                        0f, if (isPhoto) 1f else 0f,
                        1f, if (isPhoto) 1f else 0f,
                        0f, if (isPhoto) 0f else 1f,
                        1f, if (isPhoto) 0f else 1f
                    )
                    gradientTextureBuffer =
                        ByteBuffer.allocateDirect(textureData.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
                    gradientTextureBuffer.put(textureData).position(0)
                    this.gradientTopColor = gradientTopColor
                    this.gradientBottomColor = gradientBottomColor
                }
                cropState?.let {
                    if (it.useMatrix != null) {
                        useMatrixForImagePath = true
                        val verticesData = floatArrayOf(
                            0f, 0f,
                            originalWidth.toFloat(), 0f,
                            0f, originalHeight.toFloat(),
                            originalWidth.toFloat(), originalHeight.toFloat()
                        )
                        it.useMatrix.mapPoints(verticesData)
                        for (a in 0 until 4) {
                            verticesData[a * 2] = verticesData[a * 2] / w * 2f - 1f
                            verticesData[a * 2 + 1] = 1f - verticesData[a * 2 + 1] / h * 2f
                        }
                        verticesBuffer =
                            ByteBuffer.allocateDirect(verticesData.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
                        verticesBuffer.put(verticesData).position(0)
                    } else {
                        val verticesData = floatArrayOf(
                            0f, 0f,
                            w.toFloat(), 0f,
                            0f, h.toFloat(),
                            w.toFloat(), h.toFloat()
                        )
                        textureRotation = it.transformRotation

                        transformedWidth *= it.cropPw
                        transformedHeight *= it.cropPh

                        val angle = (-it.cropRotate * (Math.PI / 180.0f)).toFloat()
                        for (a in 0 until 4) {
                            val x1 = verticesData[a * 2] - w / 2
                            val y1 = verticesData[a * 2 + 1] - h / 2
                            val x2 =
                                (x1 * Math.cos(angle) - y1 * Math.sin(angle) + it.cropPx * w) * it.cropScale
                            val y2 =
                                (x1 * Math.sin(angle) + y1 * Math.cos(angle) - it.cropPy * h) * it.cropScale
                            verticesData[a * 2] = x2 / transformedWidth * 2
                            verticesData[a * 2 + 1] = y2 / transformedHeight * 2
                        }
                        verticesBuffer =
                            ByteBuffer.allocateDirect(verticesData.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
                        verticesBuffer.put(verticesData).position(0)
                    }
                } ?: run {
                    val verticesData = floatArrayOf(
                        -1.0f, -1.0f,
                        1.0f, -1.0f,
                        -1.0f, 1.0f,
                        1.0f, 1.0f
                    )
                    verticesBuffer =
                        ByteBuffer.allocateDirect(verticesData.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
                    verticesBuffer.put(verticesData).position(0)
                }
                val textureData: FloatArray = if (filterShaders != null) {
                    if (textureRotation == 90) {
                        floatArrayOf(
                            1f, 1f,
                            1f, 0f,
                            0f, 1f,
                            0f, 0f
                        )
                    } else if (textureRotation == 180) {
                        floatArrayOf(
                            1f, 0f,
                            0f, 0f,
                            1f, 1f,
                            0f, 1f
                        )
                    } else if (textureRotation == 270) {
                        floatArrayOf(
                            0f, 0f,
                            0f, 1f,
                            1f, 0f,
                            1f, 1f
                        )
                    } else {
                        floatArrayOf(
                            0f, 1f,
                            1f, 1f,
                            0f, 0f,
                            1f, 0f
                        )
                    }
                } else {
                    if (textureRotation == 90) {
                        floatArrayOf(
                            1f, 0f,
                            1f, 1f,
                            0f, 0f,
                            0f, 1f
                        )
                    } else if (textureRotation == 180) {
                        floatArrayOf(
                            1f, 1f,
                            0f, 1f,
                            1f, 0f,
                            0f, 0f
                        )
                    } else if (textureRotation == 270) {
                        floatArrayOf(
                            0f, 1f,
                            0f, 0f,
                            1f, 1f,
                            1f, 0f
                        )
                    } else {
                        floatArrayOf(
                            0f, 0f,
                            1f, 0f,
                            0f, 1f,
                            1f, 1f
                        )
                    }
                }
                if (!isPhoto && useMatrixForImagePath) {
                    textureData[1] = 1f - textureData[1]
                    textureData[3] = 1f - textureData[3]
                    textureData[5] = 1f - textureData[5]
                    textureData[7] = 1f - textureData[7]
                }
                if (cropState?.mirrored == true) {
                    for (a in 0 until 4) {
                        if (textureData[a * 2] > 0.5f) {
                            textureData[a * 2] = 0.0f
                        } else {
                            textureData[a * 2] = 1.0f
                        }
                    }
                }
                renderTextureBuffer =
                    ByteBuffer.allocateDirect(textureData.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
                renderTextureBuffer.put(textureData).position(0)
            }

            fun getTextureId(): Int {
                return mTextureID
            }

            private fun drawGradient() {
                if (NUM_GRADIENT_SHADER < 0) {
                    return
                }
                GLES20.glUseProgram(mProgram[NUM_GRADIENT_SHADER])

                GLES20.glVertexAttribPointer(maPositionHandle[NUM_GRADIENT_SHADER], 2, GLES20.GL_FLOAT, false, 8, gradientVerticesBuffer)
                GLES20.glEnableVertexAttribArray(maPositionHandle[NUM_GRADIENT_SHADER])
                GLES20.glVertexAttribPointer(maTextureHandle[NUM_GRADIENT_SHADER], 2, GLES20.GL_FLOAT, false, 8, gradientTextureBuffer)
                GLES20.glEnableVertexAttribArray(maTextureHandle[NUM_GRADIENT_SHADER])

                GLES20.glUniformMatrix4fv(muSTMatrixHandle[NUM_GRADIENT_SHADER], 1, false, mSTMatrix, 0)
                GLES20.glUniformMatrix4fv(muMVPMatrixHandle[NUM_GRADIENT_SHADER], 1, false, mMVPMatrix, 0)

                GLES20.glUniform4f(gradientTopColorHandle, Color.red(gradientTopColor) / 255f, Color.green(gradientTopColor) / 255f, Color.blue(gradientTopColor) / 255f, Color.alpha(gradientTopColor) / 255f)
                GLES20.glUniform4f(gradientBottomColorHandle, Color.red(gradientBottomColor) / 255f, Color.green(gradientBottomColor) / 255f, Color.blue(gradientBottomColor) / 255f, Color.alpha(gradientBottomColor) / 255f)
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
            }

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

            private fun drawEntity(entity: VideoEditedInfo.MediaEntity, textColor: Int) {
                if (entity.ptr != 0L) {
                    if (entity.bitmap == null || entity.W <= 0 || entity.H <= 0) {
                        return
                    }
                    RLottieDrawable.getFrame(entity.ptr, entity.currentFrame.toInt(), entity.bitmap, entity.W, entity.H, entity.bitmap.rowBytes, true)
                    applyRoundRadius(entity, entity.bitmap, if ((entity.subType and 8) != 0) textColor else 0)
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, stickerTexture[0])
                    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, entity.bitmap, 0)
                    entity.currentFrame += entity.framesPerDraw
                    if (entity.currentFrame >= entity.metadata[0]) {
                        entity.currentFrame = 0f
                    }
                    drawTexture(false, stickerTexture[0], entity.x, entity.y, entity.width, entity.height, entity.rotation, (entity.subType and 2) != 0)
                } else if (entity.animatedFileDrawable != null) {
                    val lastFrame = entity.currentFrame.toInt()
                    entity.currentFrame += entity.framesPerDraw
                    var currentFrame = entity.currentFrame.toInt()
                    while (lastFrame != currentFrame) {
                        entity.animatedFileDrawable.getNextFrame()
                        currentFrame--
                    }
                    val frameBitmap = entity.animatedFileDrawable.backgroundBitmap
                    if (frameBitmap != null) {
                        if (stickerCanvas == null && stickerBitmap != null) {
                            stickerCanvas = Canvas(stickerBitmap)
                            if (stickerBitmap.height != frameBitmap.height || stickerBitmap.width != frameBitmap.width) {
                                stickerCanvas.scale(stickerBitmap.width / frameBitmap.width.toFloat(), stickerBitmap.height / frameBitmap.height.toFloat())
                            }
                        }
                        if (stickerBitmap != null) {
                            stickerBitmap.eraseColor(Color.TRANSPARENT)
                            stickerCanvas.drawBitmap(frameBitmap, 0f, 0f, null)
                            applyRoundRadius(entity, stickerBitmap, if ((entity.subType and 8) != 0) textColor else 0)
                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, stickerTexture[0])
                            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, stickerBitmap, 0)
                            drawTexture(false, stickerTexture[0], entity.x, entity.y, entity.width, entity.height, entity.rotation, (entity.subType and 2) != 0)
                        }
                    }
                } else {
                    if (entity.bitmap != null) {
                        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, stickerTexture[0])
                        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, entity.bitmap, 0)
                        drawTexture(false, stickerTexture[0], entity.x - entity.additionalWidth / 2f, entity.y - entity.additionalHeight / 2f, entity.width + entity.additionalWidth, entity.height + entity.additionalHeight, entity.rotation, entity.type == VideoEditedInfo.MediaEntity.TYPE_PHOTO && (entity.subType and 2) != 0)
                    }
                    if (entity.entities != null && entity.entities.isNotEmpty()) {
                        for (i in entity.entities.indices) {
                            val e = entity.entities[i]
                            if (e == null) {
                                continue
                            }
                            val entity1 = e.entity
                            if (entity1 == null) {
                                continue
                            }
                            drawEntity(entity1, entity.color)
                        }
                    }
                }
            }

            private fun applyRoundRadius(entity: VideoEditedInfo.MediaEntity, stickerBitmap: Bitmap?, color: Int) {
                if (stickerBitmap == null || entity == null || entity.roundRadius == 0 && color == 0) {
                    return
                }
                if (entity.roundRadiusCanvas == null) {
                    entity.roundRadiusCanvas = Canvas(stickerBitmap)
                }
                if (entity.roundRadius != 0f) {
                    if (path == null) {
                        path = Path()
                    }
                    if (xRefPaint == null) {
                        xRefPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                        xRefPaint!!.color = 0xff000000.toInt()
                        xRefPaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                    }
                    val rad = Math.min(stickerBitmap.width, stickerBitmap.height) * entity.roundRadius
                    path!!.rewind()
                    val rect = RectF(0f, 0f, stickerBitmap.width.toFloat(), stickerBitmap.height.toFloat())
                    path!!.addRoundRect(rect, rad, rad, Path.Direction.CCW)
                    path!!.toggleInverseFillType()
                    entity.roundRadiusCanvas!!.drawPath(path!!, xRefPaint!!)
                }
                if (color != 0) {
                    if (textColorPaint == null) {
                        textColorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                        textColorPaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                    }
                    textColorPaint!!.color = color
                    entity.roundRadiusCanvas!!.drawRect(0f, 0f, stickerBitmap.width.toFloat(), stickerBitmap.height.toFloat(), textColorPaint!!)
                }
            }

            private fun drawTexture(bind: Boolean, texture: Int) {
                drawTexture(bind, texture, -10000f, -10000f, -10000f, -10000f, 0f, false)
            }

            private fun drawTexture(bind: Boolean, texture: Int, x: Float, y: Float, w: Float, h: Float, rotation: Float, mirror: Boolean) {
                drawTexture(bind, texture, x, y, w, h, rotation, mirror, false, -1)
            }

            private fun drawTexture(bind: Boolean, texture: Int, x: Float, y: Float, w: Float, h: Float, rotation: Float, mirror: Boolean, useCropMatrix: Boolean, matrixIndex: Int) {
                if (!blendEnabled) {
                    GLES20.glEnable(GLES20.GL_BLEND)
                    GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
                    blendEnabled = true
                }
                val tempX: Float
                val tempY: Float
                val tempW: Float
                val tempH: Float

                if (x <= -10000) {
                    bitmapData[0] = -1.0f
                    bitmapData[1] = 1.0f

                    bitmapData[2] = 1.0f
                    bitmapData[3] = 1.0f

                    bitmapData[4] = -1.0f
                    bitmapData[5] = -1.0f

                    bitmapData[6] = 1.0f
                    bitmapData[7] = -1.0f
                } else {
                    tempX = x * 2 - 1.0f
                    tempY = (1.0f - y) * 2 - 1.0f
                    tempW = w * 2
                    tempH = h * 2

                    bitmapData[0] = tempX
                    bitmapData[1] = tempY

                    bitmapData[2] = tempX + tempW
                    bitmapData[3] = tempY

                    bitmapData[4] = tempX
                    bitmapData[5] = tempY - tempH

                    bitmapData[6] = tempX + tempW
                    bitmapData[7] = tempY - tempH
                }
                val mx = (bitmapData[0] + bitmapData[2]) / 2f
                if (mirror) {
                    val temp = bitmapData[2]
                    bitmapData[2] = bitmapData[0]
                    bitmapData[0] = temp

                    temp = bitmapData[6]
                    bitmapData[6] = bitmapData[4]
                    bitmapData[4] = temp
                }
                if (rotation != 0f) {
                    val ratio = transformedWidth / transformedHeight.toFloat()
                    val my = (bitmapData[5] + bitmapData[1]) / 2f
                    for (a in 0 until 4) {
                        val x1 = bitmapData[a * 2] - mx
                        val y1 = (bitmapData[a * 2 + 1] - my) / ratio
                        bitmapData[a * 2] = (x1 * Math.cos(rotation.toDouble()) - y1 * Math.sin(rotation.toDouble()) + mx).toFloat()
                        bitmapData[a * 2 + 1] = ((x1 * Math.sin(rotation.toDouble()) + y1 * Math.cos(rotation.toDouble())) * ratio + my).toFloat()
                    }
                }
                bitmapVerticesBuffer.put(bitmapData).position(0)
                GLES20.glVertexAttribPointer(simplePositionHandle, 2, GLES20.GL_FLOAT, false, 8, if (matrixIndex >= 0) partsVerticesBuffer[matrixIndex] else if (useCropMatrix) verticesBuffer else bitmapVerticesBuffer)
                GLES20.glEnableVertexAttribArray(simpleInputTexCoordHandle)
                GLES20.glVertexAttribPointer(simpleInputTexCoordHandle, 2, GLES20.GL_FLOAT, false, 8, if (matrixIndex >= 0) partsTextureBuffer else if (useCropMatrix) renderTextureBuffer else textureBuffer)
                if (bind) {
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)
                }
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            fun setBreakStrategy(editText: EditTextOutline) {
                editText.breakStrategy = Layout.BREAK_STRATEGY_SIMPLE
            }

            @SuppressLint("WrongConstant")
            fun surfaceCreated() {
                for (a in mProgram.indices) {
                    var shader: String? = null
                    when (a) {
                        NUM_EXTERNAL_SHADER -> shader = FRAGMENT_EXTERNAL_SHADER
                        NUM_FILTER_SHADER -> shader = FRAGMENT_SHADER
                        NUM_GRADIENT_SHADER -> shader = GRADIENT_FRAGMENT_SHADER
                    }
                    if (shader == null) {
                        continue
                    }
                    mProgram[a] = createProgram(VERTEX_SHADER, shader, false)
                    maPositionHandle[a] = GLES20.glGetAttribLocation(mProgram[a], "aPosition")
                    maTextureHandle[a] = GLES20.glGetAttribLocation(mProgram[a], "aTextureCoord")
                    muMVPMatrixHandle[a] = GLES20.glGetUniformLocation(mProgram[a], "uMVPMatrix")
                    muSTMatrixHandle[a] = GLES20.glGetUniformLocation(mProgram[a], "uSTMatrix")
                    if (a == NUM_GRADIENT_SHADER) {
                        gradientTopColorHandle = GLES20.glGetUniformLocation(mProgram[a], "gradientTopColor")
                        gradientBottomColorHandle = GLES20.glGetUniformLocation(mProgram[a], "gradientBottomColor")
                    }
                }
                val textures = IntArray(1)
                GLES20.glGenTextures(1, textures, 0)
                mTextureID = textures[0]
                GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID)
                GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
                GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
                GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
                GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

                if (blurPath != null && cropState != null && cropState.useMatrix != null) {
                    blur = BlurringShader()
                    if (!blur.setup(transformedWidth / transformedHeight, true, 0)) {
                        blur = null
                    } else {
                        blur.updateGradient(gradientTopColor, gradientBottomColor)
                        val matrix = android.graphics.Matrix()
                        matrix.postScale(originalWidth, originalHeight)
                        matrix.postConcat(cropState.useMatrix)
                        matrix.postScale(1f / transformedWidth, 1f / transformedHeight)
                        val imatrix = android.graphics.Matrix()
                        matrix.invert(imatrix)
                        blur.updateTransform(imatrix)
                    }

                    val bitmap = BitmapFactory.decodeFile(blurPath)
                    if (bitmap != null) {
                        blurTexture = IntArray(1)
                        GLES20.glGenTextures(1, blurTexture, 0)
                        GLES20.glBindTexture(GL10.GL_TEXTURE_2D, blurTexture[0])
                        GLES20.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR)
                        GLES20.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR)
                        GLES20.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE)
                        GLES20.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE)
                        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)

                        bitmap.recycle()
                    } else {
                        blur = null
                    }

                    if (blur != null) {
                        val fragShader =
                            "varying highp vec2 vTextureCoord;" +
                            "uniform sampler2D blurImage;" +
                            "uniform sampler2D maskImage;" +
                            "void main() {" +
                            "gl_FragColor = texture2D(blurImage, vTextureCoord) * texture2D(maskImage, vTextureCoord).a;" +
                            "}"
                        val vertexShader = FilterShaders.loadShader(GLES20.GL_VERTEX_SHADER, FilterShaders.simpleVertexShaderCode)
                        val fragmentShader = FilterShaders.loadShader(GLES20.GL_FRAGMENT_SHADER, fragShader)

                        if (vertexShader != 0 && fragmentShader != 0) {
                            blurShaderProgram = GLES20.glCreateProgram()
                            GLES20.glAttachShader(blurShaderProgram, vertexShader)
                            GLES20.glAttachShader(blurShaderProgram, fragmentShader)
                            GLES20.glBindAttribLocation(blurShaderProgram, 0, "position")
                            GLES20.glBindAttribLocation(blurShaderProgram, 1, "inputTexCoord")

                            GLES20.glLinkProgram(blurShaderProgram)
                            val linkStatus = IntArray(1)
                            GLES20.glGetProgramiv(blurShaderProgram, GLES20.GL_LINK_STATUS, linkStatus, 0)
                            if (linkStatus[0] == 0) {
                                GLES20.glDeleteProgram(blurShaderProgram)
                                blurShaderProgram = 0
                            } else {
                                blurPositionHandle = GLES20.glGetAttribLocation(blurShaderProgram, "position")
                                blurInputTexCoordHandle = GLES20.glGetAttribLocation(blurShaderProgram, "inputTexCoord")
                                blurBlurImageHandle = GLES20.glGetUniformLocation(blurShaderProgram, "blurImage")
                                blurMaskImageHandle = GLES20.glGetUniformLocation(blurShaderProgram, "maskImage")

                                val verticesData = floatArrayOf(
                                    -1.0f, 1.0f,
                                    1.0f, 1.0f,
                                    -1.0f, -1.0f,
                                    1.0f, -1.0f
                                )
                                blurVerticesBuffer = ByteBuffer.allocateDirect(verticesData.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
                                blurVerticesBuffer.put(verticesData).position(0)
                            }
                        } else {
                            blur = null
                        }
                    }
                }
                if (filterShaders != null || imagePath != null || paintPath != null || mediaEntities != null || parts != null) {
                    val vertexShader = FilterShaders.loadShader(GLES20.GL_VERTEX_SHADER, FilterShaders.simpleVertexShaderCode)
                    val fragmentShader = FilterShaders.loadShader(GLES20.GL_FRAGMENT_SHADER, FilterShaders.simpleFragmentShaderCode)
                    if (vertexShader != 0 && fragmentShader != 0) {
                        simpleShaderProgram = GLES20.glCreateProgram()
                        GLES20.glAttachShader(simpleShaderProgram, vertexShader)
                        GLES20.glAttachShader(simpleShaderProgram, fragmentShader)
                        GLES20.glBindAttribLocation(simpleShaderProgram, 0, "position")
                        GLES20.glBindAttribLocation(simpleShaderProgram, 1, "inputTexCoord")

                        GLES20.glLinkProgram(simpleShaderProgram)
                        val linkStatus = IntArray(1)
                        GLES20.glGetProgramiv(simpleShaderProgram, GLES20.GL_LINK_STATUS, linkStatus, 0)
                        if (linkStatus[0] == 0) {
                            GLES20.glDeleteProgram(simpleShaderProgram)
                            simpleShaderProgram = 0
                        } else {
                            simplePositionHandle = GLES20.glGetAttribLocation(simpleShaderProgram, "position")
                            simpleInputTexCoordHandle = GLES20.glGetAttribLocation(simpleShaderProgram, "inputTexCoord")
                            simpleSourceImageHandle = GLES20.glGetUniformLocation(simpleShaderProgram, "sTexture")
                        }
                    }
                }

                if (filterShaders != null) {
                    filterShaders.create()
                    filterShaders.setRenderData(null, 0, mTextureID, originalWidth, originalHeight)
                }
                if (imagePath != null || paintPath != null) {
                    paintTexture = IntArray(if (imagePath != null) 1 else 0 + if (paintPath != null) 1 else 0)
                    GLES20.glGenTextures(paintTexture.size, paintTexture, 0)
                    try {
                        for (a in paintTexture.indices) {
                            val path: String
                            var angle = 0
                            var invert = 0
                            if (a == 0 && imagePath != null) {
                                path = imagePath
                                val orientation = AndroidUtilities.getImageOrientation(path)
                                angle = orientation.first
                                invert = orientation.second
                            } else {
                                path = paintPath
                            }
                            val bitmap = BitmapFactory.decodeFile(path)
                            if (bitmap != null) {
                                if (a == 0 && imagePath != null && !useMatrixForImagePath) {
                                    val newBitmap = Bitmap.createBitmap(transformedWidth, transformedHeight, Bitmap.Config.ARGB_8888)
                                    newBitmap.eraseColor(0xff000000)
                                    val canvas = Canvas(newBitmap)
                                    val scale: Float
                                    scale = if (angle == 90 || angle == 270) {
                                        Math.max(bitmap.height / transformedWidth.toFloat(), bitmap.width / transformedHeight.toFloat())
                                    } else {
                                        Math.max(bitmap.width / transformedWidth.toFloat(), bitmap.height / transformedHeight.toFloat())
                                    }

                                    val matrix = android.graphics.Matrix()
                                    matrix.postTranslate(-bitmap.width / 2f, -bitmap.height / 2f)
                                    matrix.postScale(if (invert == 1) -1.0f else 1.0f / scale, if (invert == 2) -1.0f else 1.0f / scale)
                                    matrix.postRotate(angle.toFloat())
                                    matrix.postTranslate(newBitmap.width / 2f, newBitmap.height / 2f)
                                    canvas.drawBitmap(bitmap, matrix, Paint(Paint.FILTER_BITMAP_FLAG))
                                    bitmap = newBitmap
                                }

                                if (a == 0 && imagePath != null) {
                                    imageWidth = bitmap.width
                                    imageHeight = bitmap.height
                                }

                                GLES20.glBindTexture(GL10.GL_TEXTURE_2D, paintTexture[a])
                                GLES20.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR)
                                GLES20.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR)
                                GLES20.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE)
                                GLES20.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE)
                                GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)
                            }
                        }
                    } catch (e: Throwable) {
                        FileLog.e(e)
                    }
                }
                if (parts != null && parts.isNotEmpty()) {
                    partsTexture = IntArray(parts.size)
                    partsVerticesBuffer = arrayOfNulls<FloatBuffer>(parts.size)
                    GLES20.glGenTextures(partsTexture.size, partsTexture, 0)
                    try {
                        for (a in partsTexture.indices) {
                            val part = parts[a]
                            val path = part.file.absolutePath

                            val opts = BitmapFactory.Options()
                            opts.inJustDecodeBounds = true
                            BitmapFactory.decodeFile(path, opts)
                            opts.inJustDecodeBounds = false
                            opts.inSampleSize = StoryEntry.calculateInSampleSize(opts, transformedWidth, transformedHeight)
                            val bitmap = BitmapFactory.decodeFile(path, opts)
                            GLES20.glBindTexture(GL10.GL_TEXTURE_2D, partsTexture[a])
                            GLES20.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR)
                            GLES20.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR)
                            GLES20.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE)
                            GLES20.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE)
                            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)

                            val verticesData = floatArrayOf(
                                0f, 0f,
                                part.width, 0f,
                                0f, part.height,
                                part.width, part.height
                            )
                            part.matrix.mapPoints(verticesData)
                            for (i in 0..3) {
                                verticesData[i * 2] = verticesData[i * 2] / transformedWidth * 2f - 1f
                                verticesData[i * 2 + 1] = 1f - verticesData[i * 2 + 1] / transformedHeight * 2f
                            }
                            partsVerticesBuffer[a] = ByteBuffer.allocateDirect(verticesData.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
                            partsVerticesBuffer[a]?.put(verticesData)?.position(0)
                        }
                    } catch (e2: Throwable) {
                        FileLog.e(e2)
                    }

                    val textureData = floatArrayOf(
                        0f, 0f,
                        1f, 0f,
                        0f, 1f,
                        1f, 1f
                    )
                    partsTextureBuffer = ByteBuffer.allocateDirect(textureData.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
                    partsTextureBuffer?.put(textureData)?.position(0)
                }
                if (mediaEntities != null) {
                    try {
                        stickerBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
                        stickerTexture = IntArray(1)
                        GLES20.glGenTextures(1, stickerTexture, 0)
                        GLES20.glBindTexture(GL10.GL_TEXTURE_2D, stickerTexture[0])
                        GLES20.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR)
                        GLES20.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR)
                        GLES20.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE)
                        GLES20.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE)
                        for (a in mediaEntities.indices) {
                            val entity = mediaEntities[a]
                            if (entity.type == VideoEditedInfo.MediaEntity.TYPE_STICKER || entity.type == VideoEditedInfo.MediaEntity.TYPE_PHOTO) {
                                initStickerEntity(entity)
                            } else if (entity.type == VideoEditedInfo.MediaEntity.TYPE_TEXT) {
                                val editText = EditTextOutline(ApplicationLoader.applicationContext)
                                editText.paint.isAntiAlias = true
                                editText.drawAnimatedEmojiDrawables = false
                                editText.setBackgroundColor(Color.TRANSPARENT)
                                editText.setPadding(AndroidUtilities.dp(7), AndroidUtilities.dp(7), AndroidUtilities.dp(7), AndroidUtilities.dp(7))
                                var typeface: Typeface?
                                if (entity.textTypeface != null && entity.textTypeface.typeface != null) {
                                    typeface = entity.textTypeface.typeface
                                    editText.setTypeface(typeface)
                                }
                                editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, entity.fontSize)
                                val text = SpannableString(entity.text)
                                for (e in entity.entities) {
                                    if (e.documentAbsolutePath == null) {
                                        continue
                                    }
                                    e.entity = VideoEditedInfo.MediaEntity()
                                    e.entity.text = e.documentAbsolutePath
                                    e.entity.subType = e.subType
                                    val span: AnimatedEmojiSpan = object : AnimatedEmojiSpan(0L, 1f, editText.paint.fontMetricsInt) {
                                        override fun draw(
                                            canvas: Canvas, charSequence: CharSequence, start: Int, end: Int,
                                            x: Float, top: Int, y: Int, bottom: Int, paint: Paint
                                        ) {
                                            super.draw(canvas, charSequence, start, end, x, top, y, bottom, paint)
                                            val tcx = entity.x + (editText.paddingLeft + x + measuredSize / 2f) / entity.viewWidth * entity.width
                                            val tcy = entity.y + (editText.paddingTop + top + (bottom - top) / 2f) / entity.viewHeight * entity.height
                                            if (entity.rotation != 0f) {
                                                val mx = entity.x + entity.width / 2f
                                                val my = entity.y + entity.height / 2f
                                                val ratio = transformedWidth / entity.viewHeight.toFloat()
                                                var x1 = tcx - mx
                                                var y1 = (tcy - my) / ratio
                                                tcx = (x1 * cos(-entity.rotation) - y1 * sin(-entity.rotation) + mx).toFloat()
                                                tcy = (x1 * sin(-entity.rotation) + y1 * cos(-entity.rotation)) * ratio + my.toFloat()
                                            }
                                            e.entity.width = (measuredSize.toFloat() / entity.viewWidth * entity.width)
                                            e.entity.height = (measuredSize.toFloat() / entity.viewHeight * entity.height)
                                            e.entity.x = tcx - e.entity.width / 2f
                                            e.entity.y = tcy - e.entity.height / 2f
                                            e.entity.rotation = entity.rotation
                                            if (e.entity.bitmap == null) initStickerEntity(e.entity)
                                        }
                                    }
                                    text.setSpan(span, e.offset, e.offset + e.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                                }
                                editText.text = Emoji.replaceEmoji(text, editText.paint.fontMetricsInt, (editText.textSize * 0.8f).toInt(), false)
                                editText.setTextColor(entity.color)
                                val text2 = editText.text
                                if (text2 is Spanned) {
                                    val spans = text2.getSpans(0, text2.length, Emoji.EmojiSpan::class.java)
                                    for (i in spans.indices) {
                                        spans[i].scale = 0.85f
                                    }
                                }

                                val gravity: Int = when (entity.textAlign) {
                                    PaintTextOptionsView.ALIGN_LEFT -> Gravity.LEFT or Gravity.CENTER_VERTICAL
                                    PaintTextOptionsView.ALIGN_CENTER -> Gravity.CENTER
                                    PaintTextOptionsView.ALIGN_RIGHT -> Gravity.RIGHT or Gravity.CENTER_VERTICAL
                                    else -> Gravity.LEFT or Gravity.CENTER_VERTICAL
                                }
                                editText.gravity = gravity
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    val textAlign: Int = when (entity.textAlign) {
                                        PaintTextOptionsView.ALIGN_LEFT -> if (LocaleController.isRTL) View.TEXT_ALIGNMENT_TEXT_END else View.TEXT_ALIGNMENT_TEXT_START
                                        PaintTextOptionsView.ALIGN_CENTER -> View.TEXT_ALIGNMENT_CENTER
                                        PaintTextOptionsView.ALIGN_RIGHT -> if (LocaleController.isRTL) View.TEXT_ALIGNMENT_TEXT_START else View.TEXT_ALIGNMENT_TEXT_END
                                        else -> if (LocaleController.isRTL) View.TEXT_ALIGNMENT_TEXT_END else View.TEXT_ALIGNMENT_TEXT_START
                                    }
                                    editText.textAlignment = textAlign
                                }
                                editText.isHorizontallyScrolling = false
                                editText.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI
                                editText.isFocusableInTouchMode = true
                                editText.inputType = editText.inputType or EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES
                                if (Build.VERSION.SDK_INT >= 23) setBreakStrategy(editText)
                                if (entity.subType == 0) {
                                    editText.frameColor = entity.color
                                    editText.setTextColor(
                                        if (AndroidUtilities.computePerceivedBrightness(entity.color) >= 0.721f) Color.BLACK else Color.WHITE
                                    )
                                } else if (entity.subType == 1) {
                                    editText.frameColor =
                                        if (AndroidUtilities.computePerceivedBrightness(entity.color) >= 0.25f) 0x99000000 else 0x99ffffff
                                    editText.setTextColor(entity.color)
                                } else if (entity.subType == 2) {
                                    editText.frameColor =
                                        if (AndroidUtilities.computePerceivedBrightness(entity.color) >= 0.25f) Color.BLACK else Color.WHITE
                                    editText.setTextColor(entity.color)
                                } else if (entity.subType == 3) {
                                    editText.frameColor = 0
                                    editText.setTextColor(entity.color)
                                }
                                editText.measure(
                                    View.MeasureSpec.makeMeasureSpec(entity.viewWidth, View.MeasureSpec.EXACTLY),
                                    View.MeasureSpec.makeMeasureSpec(entity.viewHeight, View.MeasureSpec.EXACTLY)
                                )
                                editText.layout(0, 0, entity.viewWidth, entity.viewHeight)
                                entity.bitmap =
                                    Bitmap.createBitmap(entity.viewWidth, entity.viewHeight, Bitmap.Config.ARGB_8888)
                                val canvas = Canvas(entity.bitmap)
                                editText.draw(canvas)
                            } else if (entity.type == VideoEditedInfo.MediaEntity.TYPE_VIDEO) {
                                val entityView = ImageView(ApplicationLoader.applicationContext)
                                entityView.scaleType = ImageView.ScaleType.FIT_XY
                                val b: VideoEditedInfo.EntityViewBitmapDrawable = object : VideoEditedInfo.EntityViewBitmapDrawable(entity) {
                                    override fun draw(
                                        canvas: Canvas, charSequence: CharSequence, start: Int, end: Int,
                                        x: Float, top: Int, y: Int, bottom: Int, paint: Paint
                                    ) {
                                        val time = videoTime * 1000
                                        val current = currentTime
                                        val delta = entity.animationEndTime - entity.animationStartTime
                                        if (delta == 0 || current < entity.animationStartTime || current > entity.animationEndTime) {
                                            return
                                        }
                                        val progress = ((current - entity.animationStartTime).toFloat() / delta).coerceIn(0f, 1f)
                                        val dprogress = (progress * progress * (3 - 2 * progress)).coerceIn(0f, 1f)
                                        val rotationProgress = (current.toFloat() / time).coerceIn(0f, 1f)
                                        if (entity.viewType == VideoEditedInfo.MediaEntity.VIEW_TYPE_TEXTURE) {
                                            if (entity.texture != null && entity.width != 0 && entity.height != 0) {
                                                val width = entity.width.toFloat()
                                                val height = entity.height.toFloat()
                                                var ratio = height / width
                                                if (width > height) {
                                                    ratio = width / height
                                                }
                                                val sx: Float
                                                val sy: Float
                                                val cx: Float
                                                val cy: Float
                                                if (width > height) {
                                                    sx = 1f
                                                    sy = 1f / ratio
                                                } else {
                                                    sx = ratio
                                                    sy = 1f
                                                }
                                                cx = (1f - sx) / 2f
                                                cy = (1f - sy) / 2f
                                                entity.shader.setUniforms(entity.scaleX * sx, entity.scaleY * sy, cx, cy, rotationProgress)
                                                entity.shader.useProgram()
                                                GLES20.glEnable(GLES20.GL_BLEND)
                                                GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
                                                GLES20.glDisable(GLES20.GL_DEPTH_TEST)
                                                entity.shader.setVertexAttribArray("position", 2, 0, entity.shader.verticesBuffer)
                                                entity.shader.setVertexAttribArray("inputTexCoord", 2, 0, entity.shader.textureBuffer)
                                                GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
                                                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, entity.texture)
                                                entity.shader.setUniforms()
                                                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
                                                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
                                                GLES20.glDisable(GLES20.GL_BLEND)
                                                GLES20.glEnable(GLES20.GL_DEPTH_TEST)
                                            }
                                        } else {
                                            val w = entity.width.toFloat()
                                            val h = entity.height.toFloat()
                                            val aspect = w / h
                                            val vw = entity.viewWidth.toFloat()
                                            val vh = entity.viewHeight.toFloat()
                                            val vaspect = vw / vh
                                            val fw: Float
                                            val fh: Float
                                            val cx: Float
                                            val cy: Float
                                            if (aspect < vaspect) {
                                                fw = w
                                                fh = w / vaspect
                                                cx = 0f
                                                cy = (h - fh) / (2f * h)
                                            } else {
                                                fw = h * vaspect
                                                fh = h
                                                cx = (w - fw) / (2f * w)
                                                cy = 0f
                                            }
                                            entity.shader.setUniforms(
                                                entity.scaleX,
                                                entity.scaleY,
                                                entity.x,
                                                entity.y,
                                                entity.rotation,
                                                cx,
                                                cy,
                                                fw / vw,
                                                fh / vh,
                                                entity.alpha,
                                                rotationProgress
                                            )
                                            entity.shader.useProgram()
                                            GLES20.glEnable(GLES20.GL_BLEND)
                                            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
                                            GLES20.glDisable(GLES20.GL_DEPTH_TEST)
                                            entity.shader.setVertexAttribArray("position", 2, 0, entity.shader.verticesBuffer)
                                            entity.shader.setVertexAttribArray("inputTexCoord", 2, 0, entity.shader.textureBuffer)
                                            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
                                            if (entity.bitmap != null && !entity.bitmap!!.isRecycled) {
                                                entity.shader.setTexture(entity.bitmap)
                                            }
                                            entity.shader.setUniforms()
                                            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
                                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
                                            GLES20.glDisable(GLES20.GL_BLEND)
                                            GLES20.glEnable(GLES20.GL_DEPTH_TEST)
                                        }
                                    }
                                }
                                entityView.setImageDrawable(b)
                                val view: View = if (entity.viewType == VideoEditedInfo.MediaEntity.VIEW_TYPE_VIDEO) {
                                    entityView
                                } else {
                                    val layout = LinearLayout(entityView.context)
                                    layout.layoutParams = ViewGroup.LayoutParams(entity.viewWidth, entity.viewHeight)
                                    layout.addView(entityView)
                                    layout
                                }
                                entityView.visibility = View.VISIBLE
                                entityView.measure(
                                    View.MeasureSpec.makeMeasureSpec(entity.viewWidth, View.MeasureSpec.EXACTLY),
                                    View.MeasureSpec.makeMeasureSpec(entity.viewHeight, View.MeasureSpec.EXACTLY)
                                )
                                entityView.layout(0, 0, entity.viewWidth, entity.viewHeight)
                                entity.bitmap = Bitmap.createBitmap(entity.viewWidth, entity.viewHeight, Bitmap.Config.ARGB_8888)
                                val canvas = Canvas(entity.bitmap)
                                if (entity.viewType == VideoEditedInfo.MediaEntity.VIEW_TYPE_VIDEO) {
                                    b.setEntity(entity)
                                }
                                view.draw(canvas)
                            }
                        }
                    } catch (e3: Throwable) {
                        FileLog.e(e3)
                    }
                }
                if (stickerBitmap != null && (imagePath != null || parts != null || filterShaders != null || paintPath != null)) {
                    if (filterShaders != null) {
                        initFilterEntities()
                    }
                    try {
                        stickerTexture = IntArray(1)
                        GLES20.glGenTextures(1, stickerTexture, 0)
                        GLES20.glBindTexture(GL10.GL_TEXTURE_2D, stickerTexture[0])
                        GLES20.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR)
                        GLES20.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR)
                        GLES20.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE)
                        GLES20.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE)
                        GLES20.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, 512, 512, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, null)

                        val canvas = Canvas(stickerBitmap!!)
                        for (a in mediaEntities!!.indices) {
                            val entity = mediaEntities!![a]
                            if (entity.type == VideoEditedInfo.MediaEntity.TYPE_STICKER || entity.type == VideoEditedInfo.MediaEntity.TYPE_PHOTO) {
                                drawStickerEntity(entity, canvas)
                            } else if (entity.type == VideoEditedInfo.MediaEntity.TYPE_TEXT) {
                                val editText = EditTextOutline(ApplicationLoader.applicationContext)
                                editText.paint.isAntiAlias = true
                                editText.drawAnimatedEmojiDrawables = false
                                editText.setBackgroundColor(Color.TRANSPARENT)
                                editText.setPadding(AndroidUtilities.dp(7), AndroidUtilities.dp(7), AndroidUtilities.dp(7), AndroidUtilities.dp(7))
                                var typeface: Typeface?
                                if (entity.textTypeface != null && entity.textTypeface.typeface != null) {
                                    typeface = entity.textTypeface.typeface
                                    editText.setTypeface(typeface)
                                }
                                editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, entity.fontSize)
                                val text = SpannableString(entity.text)
                                for (e in entity.entities) {
                                    if (e.documentAbsolutePath == null) {
                                        continue
                                    }
                                    e.entity = VideoEditedInfo.MediaEntity()
                                    e.entity.text = e.documentAbsolutePath
                                    e.entity.subType = e.subType
                                    val span: AnimatedEmojiSpan = object : AnimatedEmojiSpan(0L, 1f, editText.paint.fontMetricsInt) {
                                        override fun draw(
                                            canvas: Canvas, charSequence: CharSequence, start: Int, end: Int,
                                            x: Float, top: Int, y: Int, bottom: Int, paint: Paint
                                        ) {
                                            super.draw(canvas, charSequence, start, end, x, top, y, bottom, paint)
                                            val tcx = entity.x + (editText.paddingLeft + x + measuredSize / 2f) / entity.viewWidth * entity.width
                                            val tcy = entity.y + (editText.paddingTop + top + (bottom - top) / 2f) / entity.viewHeight * entity.height
                                            if (entity.rotation != 0f) {
                                                val mx = entity.x + entity.width / 2f
                                                val my = entity.y + entity.height / 2f
                                                val ratio = transformedWidth / entity.viewHeight.toFloat()
                                                var x1 = tcx - mx
                                                var y1 = (tcy - my) / ratio
                                                tcx = (x1 * cos(-entity.rotation) - y1 * sin(-entity.rotation) + mx).toFloat()
                                                tcy = (x1 * sin(-entity.rotation) + y1 * cos(-entity.rotation)) * ratio + my.toFloat()
                                            }
                                            e.entity.width = (measuredSize.toFloat() / entity.viewWidth * entity.width)
                                            e.entity.height = (measuredSize.toFloat() / entity.viewHeight * entity.height)
                                            e.entity.x = tcx - e.entity.width / 2f
                                            e.entity.y = tcy - e.entity.height / 2f
                                            e.entity.rotation = entity.rotation
                                            if (e.entity.bitmap == null) initStickerEntity(e.entity)
                                        }
                                    }
                                    text.setSpan(span, e.offset, e.offset + e.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                                }
                                editText.text = Emoji.replaceEmoji(text, editText.paint.fontMetricsInt, (editText.textSize * 0.8f).toInt(), false)
                                editText.setTextColor(entity.color)
                                val text2 = editText.text
                                if (text2 is Spanned) {
                                    val spans = text2.getSpans(0, text2.length, Emoji.EmojiSpan::class.java)
                                    for (i in spans.indices) {
                                        spans[i].scale = 0.85f
                                    }
                                }
                                val gravity: Int = when (entity.textAlign) {
                                    PaintTextOptionsView.ALIGN_LEFT -> Gravity.LEFT or Gravity.CENTER_VERTICAL
                                    PaintTextOptionsView.ALIGN_CENTER -> Gravity.CENTER
                                    PaintTextOptionsView.ALIGN_RIGHT -> Gravity.RIGHT or Gravity.CENTER_VERTICAL
                                    else -> Gravity.LEFT or Gravity.CENTER_VERTICAL
                                }
                                editText.gravity = gravity
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    val textAlign: Int = when (entity.textAlign) {
                                        PaintTextOptionsView.ALIGN_LEFT -> if (LocaleController.isRTL) View.TEXT_ALIGNMENT_TEXT_END else View.TEXT_ALIGNMENT_TEXT_START
                                        PaintTextOptionsView.ALIGN_CENTER -> View.TEXT_ALIGNMENT_CENTER
                                        PaintTextOptionsView.ALIGN_RIGHT -> if (LocaleController.isRTL) View.TEXT_ALIGNMENT_TEXT_START else View.TEXT_ALIGNMENT_TEXT_END
                                        else -> if (LocaleController.isRTL) View.TEXT_ALIGNMENT_TEXT_END else View.TEXT_ALIGNMENT_TEXT_START
                                    }
                                    editText.textAlignment = textAlign
                                }
                                editText.isHorizontallyScrolling = false
                                editText.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI
                                editText.isFocusableInTouchMode = true
                                editText.inputType = editText.inputType or EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES
                                if (Build.VERSION.SDK_INT >= 23) setBreakStrategy(editText)
                                if (entity.subType == 0) {
                                    editText.frameColor = entity.color
                                    editText.setTextColor(
                                        if (AndroidUtilities.computePerceivedBrightness(entity.color) >= 0.721f) Color.BLACK else Color.WHITE
                                    )
                                } else if (entity.subType == 1) {
                                    editText.frameColor =
                                        if (AndroidUtilities.computePerceivedBrightness(entity.color) >= 0.25f) 0x99000000 else 0x99ffffff
                                    editText.setTextColor(entity.color)
                                } else if (entity.subType == 2) {
                                    editText.frameColor =
                                        if (AndroidUtilities.computePerceivedBrightness(entity.color) >= 0.25f) Color.BLACK else Color.WHITE
                                    editText.setTextColor(entity.color)
                                } else if (entity.subType == 3) {
                                    editText.frameColor = 0
                                    editText.setTextColor(entity.color)
                                }
                                editText.measure(
                                    View.MeasureSpec.makeMeasureSpec(entity.viewWidth, View.MeasureSpec.EXACTLY),
                                    View.MeasureSpec.makeMeasureSpec(entity.viewHeight, View.MeasureSpec.EXACTLY)
                                )
                                editText.layout(0, 0, entity.viewWidth, entity.viewHeight)
                                entity.bitmap =
                                    Bitmap.createBitmap(entity.viewWidth, entity.viewHeight, Bitmap.Config.ARGB_8888)
                                val canvas = Canvas(entity.bitmap)
                                editText.draw(canvas)
                            } else if (entity.type == VideoEditedInfo.MediaEntity.TYPE_VIDEO) {
                                val entityView = ImageView(ApplicationLoader.applicationContext)
                                entityView.scaleType = ImageView.ScaleType.FIT_XY
                                val b: VideoEditedInfo.EntityViewBitmapDrawable = object : VideoEditedInfo.EntityViewBitmapDrawable(entity) {
                                    override fun draw(
                                        canvas: Canvas, charSequence: CharSequence, start: Int, end: Int,
                                        x: Float, top: Int, y: Int, bottom: Int, paint: Paint
                                    ) {
                                        val time = videoTime * 1000
                                        val current = currentTime
                                        val delta = entity.animationEndTime - entity.animationStartTime
                                        if (delta == 0 || current < entity.animationStartTime || current > entity.animationEndTime) {
                                            return
                                        }
                                        val progress = ((current - entity.animationStartTime).toFloat() / delta).coerceIn(0f, 1f)
                                        val dprogress = (progress * progress * (3 - 2 * progress)).coerceIn(0f, 1f)
                                        val rotationProgress = (current.toFloat() / time).coerceIn(0f, 1f)
                                        if (entity.viewType == VideoEditedInfo.MediaEntity.VIEW_TYPE_TEXTURE) {
                                            if (entity.texture != null && entity.width != 0 && entity.height != 0) {
                                                val width = entity.width.toFloat()
                                                val height = entity.height.toFloat()
                                                var ratio = height / width
                                                if (width > height) {
                                                    ratio = width / height
                                                }
                                                val sx: Float
                                                val sy: Float
                                                val cx: Float
                                                val cy: Float
                                                if (width > height) {
                                                    sx = 1f
                                                    sy = 1f / ratio
                                                } else {
                                                    sx = ratio
                                                    sy = 1f
                                                }
                                                cx = (1f - sx) / 2f
                                                cy = (1f - sy) / 2f
                                                entity.shader.setUniforms(entity.scaleX * sx, entity.scaleY * sy, cx, cy, rotationProgress)
                                                entity.shader.useProgram()
                                                GLES20.glEnable(GLES20.GL_BLEND)
                                                GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
                                                GLES20.glDisable(GLES20.GL_DEPTH_TEST)
                                                entity.shader.setVertexAttribArray("position", 2, 0, entity.shader.verticesBuffer)
                                                entity.shader.setVertexAttribArray("inputTexCoord", 2, 0, entity.shader.textureBuffer)
                                                GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
                                                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, entity.texture)
                                                entity.shader.setUniforms()
                                                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
                                                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
                                                GLES20.glDisable(GLES20.GL_BLEND)
                                                GLES20.glEnable(GLES20.GL_DEPTH_TEST)
                                            }
                                        } else {
                                            val w = entity.width.toFloat()
                                            val h = entity.height.toFloat()
                                            val aspect = w / h
                                            val vw = entity.viewWidth.toFloat()
                                            val vh = entity.viewHeight.toFloat()
                                            val vaspect = vw / vh
                                            val fw: Float
                                            val fh: Float
                                            val cx: Float
                                            val cy: Float
                                            if (aspect < vaspect) {
                                                fw = w
                                                fh = w / vaspect
                                                cx = 0f
                                                cy = (h - fh) / (2f * h)
                                            } else {
                                                fw = h * vaspect
                                                fh = h
                                                cx = (w - fw) / (2f * w)
                                                cy = 0f
                                            }
                                            entity.shader.setUniforms(
                                                entity.scaleX,
                                                entity.scaleY,
                                                entity.x,
                                                entity.y,
                                                entity.rotation,
                                                cx,
                                                cy,
                                                fw / vw,
                                                fh / vh,
                                                entity.alpha,
                                                rotationProgress
                                            )
                                            entity.shader.useProgram()
                                            GLES20.glEnable(GLES20.GL_BLEND)
                                            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
                                            GLES20.glDisable(GLES20.GL_DEPTH_TEST)
                                            entity.shader.setVertexAttribArray("position", 2, 0, entity.shader.verticesBuffer)
                                            entity.shader.setVertexAttribArray("inputTexCoord", 2, 0, entity.shader.textureBuffer)
                                            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
                                            if (entity.bitmap != null && !entity.bitmap!!.isRecycled) {
                                                entity.shader.setTexture(entity.bitmap)
                                            }
                                            entity.shader.setUniforms()
                                            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
                                            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
                                            GLES20.glDisable(GLES20.GL_BLEND)
                                            GLES20.glEnable(GLES20.GL_DEPTH_TEST)
                                        }
                                    }
                                }
                                entityView.setImageDrawable(b)
                                val view: View = if (entity.viewType == VideoEditedInfo.MediaEntity.VIEW_TYPE_VIDEO) {
                                    entityView
                                } else {
                                    val layout = LinearLayout(entityView.context)
                                    layout.layoutParams = ViewGroup.LayoutParams(entity.viewWidth, entity.viewHeight)
                                    layout.addView(entityView)
                                    layout
                                }
                                entityView.visibility = View.VISIBLE
                                entityView.measure(
                                    View.MeasureSpec.makeMeasureSpec(entity.viewWidth, View.MeasureSpec.EXACTLY),
                                    View.MeasureSpec.makeMeasureSpec(entity.viewHeight, View.MeasureSpec.EXACTLY)
                                )
                                entityView.layout(0, 0, entity.viewWidth, entity.viewHeight)
                                entity.bitmap = Bitmap.createBitmap(entity.viewWidth, entity.viewHeight, Bitmap.Config.ARGB_8888)
                                val canvas = Canvas(entity.bitmap)
                                if (entity.viewType == VideoEditedInfo.MediaEntity.VIEW_TYPE_VIDEO) {
                                    b.setEntity(entity)
                                }
                                view.draw(canvas)
                            }
                        }
                        GLES20.glEnable(GL10.GL_BLEND)
                        GLES20.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
                        GLES20.glDisable(GL10.GL_DEPTH_TEST)
                        GLES20.glUseProgram(MediaController.getInstance().shaderProgram)
                        MediaController.getInstance().glUniform2f(
                            MediaController.getInstance().uniformTransform, transformedX / transformedWidth, transformedY / transformedHeight
                        )
                        MediaController.getInstance().glUniform2f(
                            MediaController.getInstance().uniformColor, 1f, 1f
                        )
                        MediaController.getInstance().glUniform1i(
                            MediaController.getInstance().uniformAlpha, 1
                        )
                        GLES20.glEnableVertexAttribArray(0)
                        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, MediaController.getInstance().rectVertexBuffer)
                        GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 8, 0)
                        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
                        GLES20.glEnableVertexAttribArray(1)
                        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, MediaController.getInstance().rectTextureBuffer)
                        GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 8, 0)
                        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
                        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
                        GLES20.glDisableVertexAttribArray(0)
                        GLES20.glDisableVertexAttribArray(1)
                        GLES20.glEnable(GL10.GL_DEPTH_TEST)
                        GLES20.glDisable(GL10.GL_BLEND)
                        GLES20.glUseProgram(0)
                    } catch (e: Throwable) {
                        FileLog.e(e)
                    }
                } else {
                    GLES20.glEnable(GL10.GL_BLEND)
                    GLES20.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
                    GLES20.glDisable(GL10.GL_DEPTH_TEST)
                    GLES20.glUseProgram(MediaController.getInstance().shaderProgram)
                    MediaController.getInstance().glUniform2f(
                        MediaController.getInstance().uniformTransform, transformedX / transformedWidth, transformedY / transformedHeight
                    )
                    MediaController.getInstance().glUniform2f(
                        MediaController.getInstance().uniformColor, 1f, 1f
                    )
                    MediaController.getInstance().glUniform1i(
                        MediaController.getInstance().uniformAlpha, 1
                    )
                    GLES20.glEnableVertexAttribArray(0)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, MediaController.getInstance().rectVertexBuffer)
                    GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 8, 0)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
                    GLES20.glEnableVertexAttribArray(1)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, MediaController.getInstance().rectTextureBuffer)
                    GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 8, 0)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
                    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
                    GLES20.glDisableVertexAttribArray(0)
                    GLES20.glDisableVertexAttribArray(1)
                    GLES20.glEnable(GL10.GL_DEPTH_TEST)
                    GLES20.glDisable(GL10.GL_BLEND)
                    GLES20.glUseProgram(0)
                }
                if (videoTimelineView != null) {
                    videoTimelineView!!.updateBarStartPosition(transformedX / transformedWidth)
                    videoTimelineView!!.updateBarScale((videoWidth / transformedWidth).toFloat())
                }
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, MediaController.getInstance().frameBuffer)
                GLES20.glFramebufferTexture2D(
                    GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, MediaController.getInstance().renderBufferTexture, 0
                )
                GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
                GLES20.glUseProgram(MediaController.getInstance().shaderProgram)
                MediaController.getInstance().glUniform2f(
                    MediaController.getInstance().uniformTransform, 0f, 0f
                )
                MediaController.getInstance().glUniform2f(
                    MediaController.getInstance().uniformColor, 1f, 1f
                )
                MediaController.getInstance().glUniform1i(
                    MediaController.getInstance().uniformAlpha, 1
                )
                GLES20.glEnableVertexAttribArray(0)
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, MediaController.getInstance().rectVertexBuffer)
                GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 8, 0)
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
                GLES20.glEnableVertexAttribArray(1)
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, MediaController.getInstance().rectTextureBuffer)
                GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 8, 0)
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
                GLES20.glDisableVertexAttribArray(0)
                GLES20.glDisableVertexAttribArray(1)
                GLES20.glUseProgram(0)
                if (videoTimelineView != null) {
                    videoTimelineView!!.updateBarStartPosition(0f)
                    videoTimelineView!!.updateBarScale(1f)
                }
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
                GLES20.glEnable(GL10.GL_BLEND)
                GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
                GLES20.glDisable(GL10.GL_DEPTH_TEST)
                if (videoTimelineView != null) {
                    videoTimelineView!!.draw(canvas)
                }
                GLES20.glEnable(GL10.GL_DEPTH_TEST)
                GLES20.glDisable(GL10.GL_BLEND)
                try {
                    if (VideoEditedInfo.needRender(this, cacheImage!!)) {
                        VideoEditedInfo.renderLock.lock()
                        try {
                            cacheImage = MediaController.getInstance().createCacheForImage(this, cacheImage!!, cacheImage!!.filter, transformedWidth.toInt(), transformedHeight.toInt())
                        } finally {
                            VideoEditedInfo.renderLock.unlock()
                        }
                    }
                    val textureView2 = cacheImage!!.textureView
                    val textureWidth: Float = textureView2!!.width.toFloat()
                    val textureHeight: Float = textureView2.height.toFloat()
                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, MediaController.getInstance().frameBuffer)
                    GLES20.glFramebufferTexture2D(
                        GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, MediaController.getInstance().renderBufferTexture, 0
                    )
                    GLES20.glViewport(0, 0, MediaController.getInstance().renderBufferWidth, MediaController.getInstance().renderBufferHeight)
                    GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
                    GLES20.glUseProgram(MediaController.getInstance().shaderProgram)
                    MediaController.getInstance().glUniform2f(
                        MediaController.getInstance().uniformTransform, 0f, 0f
                    )
                    MediaController.getInstance().glUniform2f(
                        MediaController.getInstance().uniformColor, 1f, 1f
                    )
                    MediaController.getInstance().glUniform1i(
                        MediaController.getInstance().uniformAlpha, 1
                    )
                    GLES20.glEnableVertexAttribArray(0)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, MediaController.getInstance().rectVertexBuffer)
                    GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 8, 0)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
                    GLES20.glEnableVertexAttribArray(1)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, MediaController.getInstance().rectTextureBuffer)
                    GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 8, 0)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
                    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
                    GLES20.glDisableVertexAttribArray(0)
                    GLES20.glDisableVertexAttribArray(1)
                    GLES20.glUseProgram(0)
                    GLES20.glEnable(GL10.GL_BLEND)
                    GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
                    GLES20.glDisable(GL10.GL_DEPTH_TEST)
                    if (videoTimelineView != null) {
                        videoTimelineView!!.draw(canvas)
                    }
                    GLES20.glEnable(GL10.GL_DEPTH_TEST)
                    GLES20.glDisable(GL10.GL_BLEND)
                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
                    GLES20.glViewport(0, 0, textureView2.width, textureView2.height)
                    GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
                    GLES20.glUseProgram(MediaController.getInstance().shaderProgram)
                    MediaController.getInstance().glUniform2f(
                        MediaController.getInstance().uniformTransform, 0f, 0f
                    )
                    MediaController.getInstance().glUniform2f(
                        MediaController.getInstance().uniformColor, 1f, 1f
                    )
                    MediaController.getInstance().glUniform1i(
                        MediaController.getInstance().uniformAlpha, 1
                    )
                    GLES20.glEnableVertexAttribArray(0)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, MediaController.getInstance().rectVertexBuffer)
                    GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 8, 0)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
                    GLES20.glEnableVertexAttribArray(1)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, MediaController.getInstance().rectTextureBuffer)
                    GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 8, 0)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
                    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
                    GLES20.glDisableVertexAttribArray(0)
                    GLES20.glDisableVertexAttribArray(1)
                    GLES20.glUseProgram(0)
                    GLES20.glDisable(GL10.GL_BLEND)
                    GLES20.glEnable(GL10.GL_DEPTH_TEST)
                    GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT)
                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, MediaController.getInstance().frameBuffer)
                    GLES20.glFramebufferTexture2D(
                        GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, MediaController.getInstance().renderBufferTexture, 0
                    )
                    GLES20.glViewport(0, 0, MediaController.getInstance().renderBufferWidth, MediaController.getInstance().renderBufferHeight)
                    GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
                    GLES20.glUseProgram(MediaController.getInstance().blendProgram)
                    MediaController.getInstance().glUniform2f(
                        MediaController.getInstance().uniformTransform, transformedX / transformedWidth, transformedY / transformedHeight
                    )
                    MediaController.getInstance().glUniform2f(
                        MediaController.getInstance().uniformColor, 1f, 1f
                    )
                    MediaController.getInstance().glUniform1i(
                        MediaController.getInstance().uniformAlpha, 1
                    )
                    GLES20.glEnableVertexAttribArray(0)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, MediaController.getInstance().rectVertexBuffer)
                    GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 8, 0)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
                    GLES20.glEnableVertexAttribArray(1)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, MediaController.getInstance().rectTextureBuffer)
                    GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 8, 0)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
                    GLES20.glEnableVertexAttribArray(2)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, MediaController.getInstance().rectVertexBuffer)
                    GLES20.glVertexAttribPointer(2, 2, GLES20.GL_FLOAT, false, 8, 0)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
                    GLES20.glEnableVertexAttribArray(3)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, MediaController.getInstance().rectTextureBuffer)
                    GLES20.glVertexAttribPointer(3, 2, GLES20.GL_FLOAT, false, 8, 0)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
                    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
                    GLES20.glDisableVertexAttribArray(0)
                    GLES20.glDisableVertexAttribArray(1)
                    GLES20.glDisableVertexAttribArray(2)
                    GLES20.glDisableVertexAttribArray(3)
                    GLES20.glUseProgram(0)
                    GLES20.glDisable(GL10.GL_BLEND)
                    GLES20.glEnable(GL10.GL_DEPTH_TEST)
                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
                    GLES20.glViewport(0, 0, textureView2.width, textureView2.height)
                    GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
                    GLES20.glUseProgram(MediaController.getInstance().blendProgram)
                    MediaController.getInstance().glUniform2f(
                        MediaController.getInstance().uniformTransform, 0f, 0f
                    )
                    MediaController.getInstance().glUniform2f(
                        MediaController.getInstance().uniformColor, 1f, 1f
                    )
                    MediaController.getInstance().glUniform1i(
                        MediaController.getInstance().uniformAlpha, 1
                    )
                    GLES20.glEnableVertexAttribArray(0)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, MediaController.getInstance().rectVertexBuffer)
                    GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 8, 0)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
                    GLES20.glEnableVertexAttribArray(1)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, MediaController.getInstance().rectTextureBuffer)
                    GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 8, 0)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
                    GLES20.glEnableVertexAttribArray(2)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, MediaController.getInstance().rectVertexBuffer)
                    GLES20.glVertexAttribPointer(2, 2, GLES20.GL_FLOAT, false, 8, 0)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
                    GLES20.glEnableVertexAttribArray(3)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, MediaController.getInstance().rectTextureBuffer)
                    GLES20.glVertexAttribPointer(3, 2, GLES20.GL_FLOAT, false, 8, 0)
                    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
                    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
                    GLES20.glDisableVertexAttribArray(0)
                    GLES20.glDisableVertexAttribArray(1)
                    GLES20.glDisableVertexAttribArray(2)
                    GLES20.glDisableVertexAttribArray(3)
                    GLES20.glUseProgram(0)
                    GLES20.glEnable(GL10.GL_BLEND)
                    GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
                    GLES20.glDisable(GL10.GL_DEPTH_TEST)
                    if (videoTimelineView != null) {
                        videoTimelineView!!.draw(canvas)
                    }
                    GLES20.glEnable(GL10.GL_DEPTH_TEST)
                    GLES20.glDisable(GL10.GL_BLEND)
                } catch (e: Exception) {
                    FileLog.e(e)
                }
                return true
            }

            private fun initStickerEntity(entity: VideoEditedInfo.MediaEntity) {
                entity.W = (entity.width * transformedWidth).toInt()
                entity.H = (entity.height * transformedHeight).toInt()
                if (entity.W > 512) {
                    entity.H = (entity.H / entity.W.toFloat() * 512).toInt()
                    entity.W = 512
                }
                if (entity.H > 512) {
                    entity.W = (entity.W / entity.H.toFloat() * 512).toInt()
                    entity.H = 512
                }
                if (entity.subType and 1 != 0) {
                    if (entity.W <= 0 || entity.H <= 0) {
                        return
                    }
                    entity.bitmap = Bitmap.createBitmap(entity.W, entity.H, Bitmap.Config.ARGB_8888)
                    entity.metadata = IntArray(3)
                    entity.ptr = RLottieDrawable.create(
                        entity.text,
                        null,
                        entity.W,
                        entity.H,
                        entity.metadata,
                        false,
                        null,
                        false,
                        0
                    )
                    entity.framesPerDraw = entity.metadata[1] / videoFps
                } else if (entity.subType and 4 != 0) {
                    entity.animatedFileDrawable = AnimatedFileDrawable(
                        File(entity.text),
                        true,
                        0,
                        0,
                        null,
                        null,
                        null,
                        0,
                        UserConfig.selectedAccount,
                        true,
                        512,
                        512,
                        null
                    )
                    entity.framesPerDraw = entity.animatedFileDrawable.fps / videoFps
                    entity.currentFrame = 0
                    entity.animatedFileDrawable.getNextFrame()
                } else {
                    if (Build.VERSION.SDK_INT >= 19) {
                        val opts = BitmapFactory.Options()
                        if (entity.type == VideoEditedInfo.MediaEntity.TYPE_PHOTO) {
                            opts.inMutable = true
                        }
                        entity.bitmap = BitmapFactory.decodeFile(entity.text, opts)
                    } else {
                        try {
                            val path = File(entity.text)
                            val file = RandomAccessFile(path, "r")
                            val buffer = file.channel.map(
                                FileChannel.MapMode.READ_ONLY,
                                0,
                                path.length()
                            )
                            val bmOptions = BitmapFactory.Options()
                            bmOptions.inJustDecodeBounds = true
                            Utilities.loadWebpImage(null, buffer, buffer.limit(), bmOptions, true)
                            if (entity.type == VideoEditedInfo.MediaEntity.TYPE_PHOTO) {
                                bmOptions.inMutable = true
                            }
                            entity.bitmap = Bitmaps.createBitmap(
                                bmOptions.outWidth,
                                bmOptions.outHeight,
                                Bitmap.Config.ARGB_8888
                            )
                            Utilities.loadWebpImage(
                                entity.bitmap,
                                buffer,
                                buffer.limit(),
                                null,
                                true
                            )
                            file.close()
                        } catch (e: Throwable) {
                            FileLog.e(e)
                        }
                    }
                    if (entity.type == VideoEditedInfo.MediaEntity.TYPE_PHOTO && entity.bitmap != null) {
                        entity.roundRadius =
                            AndroidUtilities.dp(12) / Math.min(entity.viewWidth, entity.viewHeight).toFloat()
                        val orientation = AndroidUtilities.getImageOrientation(entity.text)
                        entity.rotation -= Math.toRadians(orientation.first.toDouble())
                        if (orientation.first / 90 % 2 == 1) {
                            val cx = entity.x + entity.width / 2f
                            val cy = entity.y + entity.height / 2f

                            val w = entity.width * transformedWidth / transformedHeight
                            entity.width = entity.height * transformedHeight / transformedWidth
                            entity.height = w

                            entity.x = cx - entity.width / 2f
                            entity.y = cy - entity.height / 2f
                        }
                        applyRoundRadius(entity, entity.bitmap, 0)
                    } else if (entity.bitmap != null) {
                        val aspect = entity.bitmap.width / entity.bitmap.height.toFloat()
                        if (aspect > 1) {
                            val h = entity.height / aspect
                            entity.y += (entity.height - h) / 2
                            entity.height = h
                        } else if (aspect < 1) {
                            val w = entity.width * aspect
                            entity.x += (entity.width - w) / 2
                            entity.width = w
                        }
                    }
                }
            }

            private fun createProgram(vertexSource: String, fragmentSource: String, is300: Boolean): Int {
                if (is300) {
                    val vertexShader = FilterShaders.loadShader(GLES30.GL_VERTEX_SHADER, vertexSource)
                    if (vertexShader == 0) {
                        return 0
                    }
                    val pixelShader = FilterShaders.loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource)
                    if (pixelShader == 0) {
                        return 0
                    }
                    val program = GLES30.glCreateProgram()
                    if (program == 0) {
                        return 0
                    }
                    GLES30.glAttachShader(program, vertexShader)
                    GLES30.glAttachShader(program, pixelShader)
                    GLES30.glLinkProgram(program)
                    val linkStatus = IntArray(1)
                    GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0)
                    if (linkStatus[0] != GLES30.GL_TRUE) {
                        GLES30.glDeleteProgram(program)
                        return 0
                    }
                    return program
                } else {
                    val vertexShader = FilterShaders.loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
                    if (vertexShader == 0) {
                        return 0
                    }
                    val pixelShader = FilterShaders.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
                    if (pixelShader == 0) {
                        return 0
                    }
                    val program = GLES20.glCreateProgram()
                    if (program == 0) {
                        return 0
                    }
                    GLES20.glAttachShader(program, vertexShader)
                    GLES20.glAttachShader(program, pixelShader)
                    GLES20.glLinkProgram(program)
                    val linkStatus = IntArray(1)
                    GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
                    if (linkStatus[0] != GLES20.GL_TRUE) {
                        GLES20.glDeleteProgram(program)
                        return 0
                    }
                    return program
                }
            }

            fun release() {
                mediaEntities?.forEach { entity ->
                    if (entity.ptr != 0) {
                        RLottieDrawable.destroy(entity.ptr)
                    }
                    entity.animatedFileDrawable?.recycle()
                    if (entity.view is EditTextEffects) {
                        (entity.view as EditTextEffects).recycleEmojis()
                    }
                    entity.bitmap?.recycle()
                    entity.bitmap = null
                }
            }

            fun changeFragmentShader(fragmentExternalShader: String, fragmentShader: String, is300: Boolean) {
                if (NUM_EXTERNAL_SHADER in 0 until mProgram.size) {
                    val newProgram = createProgram(
                        if (is300) VERTEX_SHADER_300 else VERTEX_SHADER,
                        fragmentExternalShader,
                        is300
                    )
                    if (newProgram != 0) {
                        GLES20.glDeleteProgram(mProgram[NUM_EXTERNAL_SHADER])
                        mProgram[NUM_EXTERNAL_SHADER] = newProgram

                        texSizeHandle = GLES20.glGetUniformLocation(newProgram, "texSize")
                    }
                }
                if (NUM_FILTER_SHADER in 0 until mProgram.size) {
                    val newProgram = createProgram(
                        if (is300) VERTEX_SHADER_300 else VERTEX_SHADER,
                        fragmentShader,
                        is300
                    )
                    if (newProgram != 0) {
                        GLES20.glDeleteProgram(mProgram[NUM_FILTER_SHADER])
                        mProgram[NUM_FILTER_SHADER] = newProgram
                    }
                }
            }
        }

    """.trimIndent()

    @Test
    fun `should expect brain class`(){
//        val url = listOfUrlClassTest[1]
//        val client = HttpClient.newBuilder().build();
//        val request = HttpRequest.newBuilder()
//            .uri(URI.create(url))
//            .build();
//
//        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
//        val code = response.body()

        val ktFile = compileContentForTest(code)
        MetricProcessor().onProcess(ktFile)

        val BMCOUNT = ktFile.getUserData(MetricProcessor.numberOfBrainMethodCount) ?: -1
        val LOC = ktFile.getUserData(MetricProcessor.numberOfLineOfCode) ?: -1
        val WMC = ktFile.getUserData(MetricProcessor.numberOfWeightedMethodCount) ?: -1
        val TCC = ktFile.getUserData(MetricProcessor.numberOfTightClassCohesion) ?: -1.0

        println("BMCOUNT : $BMCOUNT")
        println("LOC : $LOC")
        println("WMC : $WMC")
        println("TCC : $TCC")

        val brainClass = BrainClass(Config.empty)
        assert(brainClass.isDetected(BMCOUNT, LOC, WMC, TCC))
    }
}
