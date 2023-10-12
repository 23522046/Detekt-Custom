package org.example.detekt

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Config
import org.example.detekt.smells.FeatureEnvy
import org.junit.jupiter.api.Test

class FeatureEnvyTest {
    @Test
    fun `should expect feature envy`(){
        val codeOld = """
            class A {
                private var a: Int = 0
                private var b: Int = 0
                private var c: Int = 0

                fun M(){ 
                    val a = a - b
                    val d = a + b
                    
                    val objB = B()
                    val e = objB.k
                    
                    val objC = C()
                    val f = objC.t + objC.u + objC.v
                }
            }
        """.trimIndent()

        val code = """
            class AudioService : Service(), OnCompletionListener, OnPreparedListener,
              MediaPlayer.OnErrorListener, AudioFocusable, OnSeekCompleteListener {
              // our media player
              private var player: MediaPlayer? = null

              // are we playing an override file (basmalah/isti3atha)
              private var playerOverride = false

              // object representing the current playing request
              private var audioRequest: AudioRequest? = null

              // the playback queue
              private var audioQueue: AudioQueue? = null

              private var state = State.Stopped

              private var audioFocus = AudioFocus.NoFocusNoDuck

              // are we already in the foreground
              private var isSetupAsForeground = false

              // should we stop (after preparing is done) or not
              private var shouldStop = false

              // The ID we use for the notification (the onscreen alert that appears
              // at the notification area at the top of the screen as an icon -- and
              // as text as well if the user expands the notification area).
              private val NOTIFICATION_ID = Constants.NOTIFICATION_ID_AUDIO_PLAYBACK

              // Wifi lock that we hold when streaming files from the internet,
              // in order to prevent the device from shutting off the Wifi radio
              private lateinit var wifiLock: WifiLock

              private lateinit var audioFocusHelper: AudioFocusHelper
              private lateinit var notificationManager: NotificationManager
              private lateinit var broadcastManager: LocalBroadcastManager
              private lateinit var noisyAudioStreamReceiver: BroadcastReceiver
              private lateinit var mediaSession: MediaSessionCompat

              private lateinit var serviceLooper: Looper
              private lateinit var serviceHandler: ServiceHandler

              private var notificationBuilder: NotificationCompat.Builder? = null
              private var pausedNotificationBuilder: NotificationCompat.Builder? = null
              private var didSetNotificationIconOnNotificationBuilder = false
              private var gaplessSura = 0
              private var notificationColor = 0

              // read by service thread, written on the I/O thread once
              @Volatile
              private var notificationIcon: Bitmap? = null
              private var displayIcon: Bitmap? = null
              private var gaplessSuraData: SparseIntArray = SparseIntArray()
              private var timingDisposable: Disposable? = null
              private val compositeDisposable = CompositeDisposable()

              @Inject
              lateinit var quranInfo: QuranInfo

              @Inject
              lateinit var quranDisplayData: QuranDisplayData

              @Inject
              lateinit var audioUtils: AudioUtils

              @Inject
              lateinit var audioEventPresenter: AudioEventPresenter

              override fun onCreate() {
                Timber.i("debug: Creating service")
                val thread = HandlerThread(
                  "AyahAudioService",
                  Process.THREAD_PRIORITY_BACKGROUND
                )
                thread.start()

                // Get the HandlerThread's Looper and use it for our Handler
                serviceLooper = thread.looper
                serviceHandler = ServiceHandler(serviceLooper)
                val appContext = applicationContext
                (appContext as QuranApplication).applicationComponent.inject(this)
                wifiLock = (appContext.getSystemService(WIFI_SERVICE) as WifiManager)
                  .createWifiLock(WifiManager.WIFI_MODE_FULL, "QuranAudioLock")
                notificationManager =
                  appContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

                // create the Audio Focus Helper
                audioFocusHelper = AudioFocusHelper(appContext, this)
                broadcastManager = LocalBroadcastManager.getInstance(appContext)
                noisyAudioStreamReceiver = NoisyAudioStreamReceiver()
                registerReceiver(
                  noisyAudioStreamReceiver,
                  IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
                )
                val receiver = ComponentName(this, MediaButtonReceiver::class.java)
                mediaSession = MediaSessionCompat(appContext, "QuranMediaSession", receiver, null)
                mediaSession.setCallback(MediaSessionCallback(), serviceHandler)
                val channelName = getString(R.string.notification_channel_audio)
                setupNotificationChannel(
                  notificationManager, NOTIFICATION_CHANNEL_ID, channelName
                )
                notificationColor = ContextCompat.getColor(this, R.color.audio_notification_color)
                try {
                  // for Android Wear, use a 1x1 Bitmap with the notification color
                  val placeholder = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                  displayIcon = placeholder
                  val canvas = Canvas(placeholder)
                  canvas.drawColor(notificationColor)
                } catch (oom: OutOfMemoryError) {
                  Timber.e(oom)
                }

                val icon = displayIcon
                // if we couldn't load the 1x1 bitmap, we can't load the image either
                if (icon != null) {
                  compositeDisposable.add(
                    Maybe.fromCallable { generateNotificationIcon() ?: icon }
                      .subscribeOn(Schedulers.io())
                      .subscribe { bitmap: Bitmap? -> notificationIcon = bitmap })
                }
              }
            }
        """.trimIndent()

        val ktFile = compileContentForTest(code)
        MetricProcessor().onProcess(ktFile)

        val LAA = ktFile.getUserData(MetricProcessor.numberOfLocalityOfAttributeAccesses) ?: -1.0
        val ATFD = ktFile.getUserData(MetricProcessor.numberOfAccessToForeignData) ?: -1
        val FDP = ktFile.getUserData(MetricProcessor.numberOfForeignDataProviders) ?: -1

        println("LAA : $LAA")
        println("ATFD : $ATFD")
        println("FDP : $FDP")

        val featureEnvy = FeatureEnvy(Config.empty)

        assert(featureEnvy.isDetected(ATFD, LAA, FDP))
    }
}
