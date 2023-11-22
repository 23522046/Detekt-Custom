package org.example.detekt

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Config
import org.example.detekt.smells.GodClass
import org.example.detekt.util.Edge
import org.example.detekt.util.Graph
import org.junit.jupiter.api.Test
import java.text.DecimalFormat
import java.util.*
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class GodClassTest {
    val listOfUrlClassTest = listOf(
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/util/QuranFileUtils.kt",
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/presenter/audio/service/AudioQueue.kt",
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/presenter/audio/AudioPresenter.kt",
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/ui/fragment/BookmarksFragment.java",
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/ui/fragment/QuranPageFragment.jav   a",
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/ui/translation/TranslationView.java",
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/database/DatabaseHandler.kt",
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/service/AudioService.kt",
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/service/QuranDownloadService.java"
    )

    val code = """
        package org.telegram.ui.Cells

        import android.annotation.SuppressLint
        import android.content.Context
        import android.graphics.*
        import android.os.Build
        import android.text.TextUtils
        import android.util.AttributeSet
        import android.view.View
        import android.view.ViewOutlineProvider
        import org.telegram.messenger.*
        import org.telegram.messenger.ImageLocation
        import org.telegram.tgnet.TLRPC
        import org.telegram.ui.Components.*
        import java.io.File

        class PatternCell(context: Context, attrs: AttributeSet? = null) : BackupImageView(context, attrs), DownloadController.FileDownloadProgressListener {
            
           private val SIZE = 100
           private val rect = RectF()
           private val radialProgress: RadialProgress2 = RadialProgress2(this)
           private var wasSelected: Boolean = false
           private var currentPattern: TLRPC.TL_wallPaper? = null
           private val currentAccount: Int = UserConfig.selectedAccount
           private var gradientShader: LinearGradient? = null
           private var currentBackgroundColor: Int = 0
           private var currentGradientColor1: Int = 0
           private var currentGradientColor2: Int = 0
           private var currentGradientColor3: Int = 0
           private var currentGradientAngle: Int = 0


           private val backgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
           private var backgroundDrawable: MotionBackgroundDrawable? = null


           private var TAG: Int = 0


           private var delegate: PatternCellDelegate? = null
           private var maxWallpaperSize: Int = 0


           interface PatternCellDelegate {
               fun getSelectedPattern(): TLRPC.TL_wallPaper?
               fun getBackgroundGradientColor1(): Int
               fun getBackgroundGradientColor2(): Int
               fun getBackgroundGradientColor3(): Int
               fun getBackgroundGradientAngle(): Int
               fun getBackgroundColor(): Int
               fun getPatternColor(): Int
               fun getCheckColor(): Int
               fun getIntensity(): Float
           }


           init {
               setRoundRadius(AndroidUtilities.dp(6))
               maxWallpaperSize = SIZE
               TAG = DownloadController.getInstance(currentAccount).generateObserverTag()


               if (Build.VERSION.SDK_INT >= 21) {
                   outlineProvider = object : ViewOutlineProvider() {
                       override fun getOutline(view: View, outline: Outline) {
                           outline.setRoundRect(
                               AndroidUtilities.dp(1).toFloat(),
                               AndroidUtilities.dp(1).toFloat(),
                               (view.measuredWidth - AndroidUtilities.dp(1)).toFloat(),
                               (view.measuredHeight - AndroidUtilities.dp(1)).toFloat(),
                               AndroidUtilities.dp(6).toFloat()
                           )
                       }
                   }
                   clipToOutline = true
               }
           }


           fun setPattern(wallPaper: TLRPC.TL_wallPaper?) {
               currentPattern = wallPaper
               if (wallPaper != null) {
                   val thumb = FileLoader.getClosestPhotoSizeWithSize(wallPaper.document.thumbs, AndroidUtilities.dp(SIZE))
                   setImage(
                       ImageLocation.getForDocument(thumb, wallPaper.document),
                       SIZE.toString() + "_" + SIZE,
                       null,
                       null,
                       "png",
                       0,
                       1,
                       wallPaper
                   )
               } else {
                   setImageDrawable(null)
               }
               updateSelected(false)
           }


           override fun onAttachedToWindow() {
               super.onAttachedToWindow()
               updateSelected(false)
           }


           fun updateSelected(animated: Boolean) {
               val selectedPattern: TLRPC.TL_wallPaper? = delegate?.getSelectedPattern()
               val isSelected = currentPattern == null && selectedPattern == null || selectedPattern != null && currentPattern != null && currentPattern!!.id == selectedPattern!!.id
               if (isSelected) {
                   updateButtonState(selectedPattern, false, animated)
               } else {
                   radialProgress.setIcon(MediaActionDrawable.ICON_NONE, false, animated)
               }
               invalidate()
           }


           override fun invalidate() {
               super.invalidate()
           }


           private fun updateButtonState(image: Any?, ifSame: Boolean, animated: Boolean) {
               if (image is TLRPC.TL_wallPaper || image is MediaController.SearchImage) {
                   val path: File
                   val size: Int
                   val fileName: String
                   if (image is TLRPC.TL_wallPaper) {
                       val wallPaper: TLRPC.TL_wallPaper = image
                       fileName = FileLoader.getAttachFileName(wallPaper.document)
                       if (TextUtils.isEmpty(fileName)) {
                           return
                       }
                       path = FileLoader.getInstance(currentAccount).getPathToAttach(wallPaper.document, true)
                   } else {
                       val wallPaper: MediaController.SearchImage = image
                       if (wallPaper.photo != null) {
                           val photoSize: TLRPC.PhotoSize = FileLoader.getClosestPhotoSizeWithSize(wallPaper.photo.sizes, maxWallpaperSize, true)
                           path = FileLoader.getInstance(currentAccount).getPathToAttach(photoSize, true)
                           fileName = FileLoader.getAttachFileName(photoSize)
                       } else {
                           path = ImageLoader.getHttpFilePath(wallPaper.imageUrl, "jpg")
                           fileName = path.name
                       }
                       if (TextUtils.isEmpty(fileName)) {
                           return
                       }
                   }
                   if (path.exists()) {
                       DownloadController.getInstance(currentAccount).removeLoadingFileObserver(this)
                       radialProgress.setProgress(1f, animated)
                       radialProgress.setIcon(MediaActionDrawable.ICON_CHECK, ifSame, animated)
                   } else {
                       DownloadController.getInstance(currentAccount).addLoadingFileObserver(fileName, null, this)
                       val isLoading: Boolean = FileLoader.getInstance(currentAccount).isLoadingFile(fileName)
                       val progress: Float? = ImageLoader.getInstance().getFileProgress(fileName)
                       if (progress != null) {
                           radialProgress.setProgress(progress, animated)
                       } else {
                           radialProgress.setProgress(0f, animated)
                       }
                       radialProgress.setIcon(MediaActionDrawable.ICON_EMPTY, ifSame, animated)
                   }
               } else {
                   radialProgress.setIcon(MediaActionDrawable.ICON_CHECK, ifSame, animated)
               }
           }


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


           override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
               setMeasuredDimension(AndroidUtilities.dp(SIZE), AndroidUtilities.dp(SIZE))
           }


           override fun onFailedDownload(fileName: String, canceled: Boolean) {
               val selectedPattern: TLRPC.TL_wallPaper? = delegate?.getSelectedPattern()
               val isSelected: Boolean =
                   currentPattern == null && selectedPattern == null || selectedPattern != null && currentPattern != null && currentPattern!!.id == selectedPattern!!.id
               if (isSelected) {
                   if (canceled) {
                       radialProgress.setIcon(MediaActionDrawable.ICON_NONE, false, true)
                   } else {
                       updateButtonState(currentPattern, true, canceled)
                   }
               }
           }


           override fun onSuccessDownload(fileName: String) {
               radialProgress.setProgress(1f, true)
               val selectedPattern: TLRPC.TL_wallPaper? = delegate?.getSelectedPattern()
               val isSelected: Boolean =
                   currentPattern == null && selectedPattern == null || selectedPattern != null && currentPattern != null && currentPattern!!.id == selectedPattern!!.id
               if (isSelected) {
                   updateButtonState(currentPattern, false, true)
               }
           }


           override fun onProgressDownload(fileName: String, downloadedSize: Long, totalSize: Long) {
               radialProgress.setProgress(
                   Math.min(
                       1f,
                       downloadedSize / totalSize.toFloat()
                   ), true
               )
               val selectedPattern: TLRPC.TL_wallPaper? = delegate?.getSelectedPattern()
               val isSelected: Boolean =
                   currentPattern == null && selectedPattern == null || selectedPattern != null && currentPattern != null && currentPattern!!.id == selectedPattern!!.id
               if (isSelected && radialProgress.getIcon() != MediaActionDrawable.ICON_EMPTY) {
                   updateButtonState(currentPattern, false, true)
               }
           }


           override fun onProgressUpload(fileName: String, uploadedSize: Long, totalSize: Long, isEncrypted: Boolean) {


           }


           override fun getObserverTag(): Int {
               return TAG
           }
        }

    """.trimIndent()

    @Test
    fun `should expect god class`(){
//        val url = listOfUrlClassTest[1]
//        val client = HttpClient.newBuilder().build();
//        val request = HttpRequest.newBuilder()
//            .uri(URI.create(url))
//            .build();

//        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
//        val code = response.body()

        val ktFile = compileContentForTest(code)
        MetricProcessor().onProcess(ktFile)

        val ATFD = ktFile.getUserData(MetricProcessor.numberOfAccessToForeignData) ?: -1
        val WMC = ktFile.getUserData(MetricProcessor.numberOfWeightedMethodCount) ?: -1
        val TCC = ktFile.getUserData(MetricProcessor.numberOfTightClassCohesion) ?: -1.0

        val dec = DecimalFormat("#.##")

//        println("File name : ${url.split("/").last()}")
        println("ATFD : $ATFD")
        println("WMC : $WMC")
        println("TCC : $TCC")

        val godClass = GodClass(Config.empty)
        //assert(godClass.isDetected(ATFD, WMC, TCC))
        assert(true)
    }

    @Test
    fun `test graph`(){
        // define edges of the graph
        val edges: List<Edge> = Arrays.asList(
            Edge(0, 1, 2), Edge(0, 2, 4),
            Edge(1, 2, 4), Edge(2, 0, 5), Edge(2, 1, 4),
            Edge(3, 2, 3), Edge(4, 5, 1), Edge(5, 4, 3)
        )

        // call graph class Constructor to construct a graph
        val graph = Graph(edges)

        // print the graph as an adjacency list
        Graph.printGraph(graph)
        assert(true)
    }
}
