package org.example.detekt

import io.github.detekt.test.utils.compileContentForTest
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

    @Test
    fun `should expect brain class`(){
        val url = listOfUrlClassTest[0]
        val client = HttpClient.newBuilder().build();
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val code = response.body()

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

        assert(BrainClass.isDetected(BMCOUNT, LOC, WMC, TCC))
    }
}
