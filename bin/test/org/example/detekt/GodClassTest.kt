package org.example.detekt

import io.github.detekt.test.utils.compileContentForTest
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
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/ui/QuranActivity.kt",
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/ui/fragment/BookmarksFragment.java",
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/ui/fragment/QuranPageFragment.java",
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/ui/translation/TranslationView.java",
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/database/DatabaseHandler.kt",
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/service/AudioService.kt",
        "https://raw.githubusercontent.com/quran/quran_android/master/app/src/main/java/com/quran/labs/androidquran/service/QuranDownloadService.java"
    )

    @Test
    fun `should expect god class`(){
        val url = listOfUrlClassTest[0]
        val client = HttpClient.newBuilder().build();
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val code = response.body()

        val ktFile = compileContentForTest(code)
        MetricProcessor().onProcess(ktFile)

        val ATFD = ktFile.getUserData(MetricProcessor.numberOfAccessToForeignData) ?: -1
        val WMC = ktFile.getUserData(MetricProcessor.numberOfWeightedMethodCount) ?: -1
        val TCC = ktFile.getUserData(MetricProcessor.numberOfTightClassCohesion) ?: -1.0

        val dec = DecimalFormat("#.##")

        println("File name : ${url.split("/").last()}")
        println("ATFD : $ATFD")
        println("WMC : $WMC")
        println("TCC : $TCC")
        assert(GodClass.isDetected(ATFD, WMC, TCC))
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
