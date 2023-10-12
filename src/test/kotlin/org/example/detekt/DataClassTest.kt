package org.example.detekt

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Config
import org.example.detekt.metrics.NumberOfPublicAttribute
import org.example.detekt.smells.DataClass
import org.junit.jupiter.api.Test
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class DataClassTest {
    val listOfUrlClassTest = listOf(
        "https://raw.githubusercontent.com/23522046/IF5250_RAJIN/main/app/src/main/java/org/informatika/if5250rajinapps/model/Presence.kt",
        "https://raw.githubusercontent.com/23522046/IF5250_RAJIN/main/app/src/main/java/org/informatika/if5250rajinapps/viewmodel/RequestViewModel.kt",
        "https://raw.githubusercontent.com/23522046/IF5250_RAJIN/main/app/src/main/java/org/informatika/if5250rajinapps/activity/MainActivity.kt"
    )

    @Test
    fun `should expect data class`(){
        val url = listOfUrlClassTest[2]
        val client = HttpClient.newBuilder().build();
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val code = response.body()

        val ktFile = compileContentForTest(code)
        MetricProcessor().onProcess(ktFile)

        val NOPA = ktFile.getUserData(MetricProcessor.numberOfPublicAttribute) ?: -1
        val NOAM = ktFile.getUserData(MetricProcessor.numberOfAccessorMethod) ?: -1
        val WOC = ktFile.getUserData(MetricProcessor.numberOfWeightOfClass) ?: -1.0
        val WMC = ktFile.getUserData(MetricProcessor.numberOfWeightedMethodCount) ?: -1

        println("File name : ${url.split("/").last()}")
        println("NOPA : $NOPA")
        println("NOAM : $NOAM")
        println("WOC : $WOC")
        println("WMC : $WMC")

        val dataClass = DataClass(Config.empty)
        assert(dataClass.isDetected(WOC, WMC, NOPA, NOAM))
    }
}
