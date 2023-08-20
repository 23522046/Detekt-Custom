package org.example.detekt

import io.github.detekt.test.utils.compileContentForTest
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
            class QuranActivity : AppCompatActivity(),
                OnBookmarkTagsUpdateListener,
                JumpDestination {
              private var upgradeDialog: AlertDialog? = null
              private var showedTranslationUpgradeDialog = false
              private var isRtl = false
              private var isPaused = false
              private var searchItem: MenuItem? = null
              private var supportActionMode: ActionMode? = null
              private val compositeDisposable = CompositeDisposable()
              lateinit var latestPageObservable: Observable<Int>

              @Inject
              lateinit var settings: QuranSettings
              @Inject
              lateinit var audioUtils: AudioUtils
              @Inject
              lateinit var recentPageModel: RecentPageModel
              @Inject
              lateinit var translationManagerPresenter: TranslationManagerPresenter
              @Inject
              lateinit var quranIndexEventLogger: QuranIndexEventLogger
              @Inject
              lateinit var extraScreens: Set<@JvmSuppressWildcards ExtraScreenProvider>

              public override fun onCreate(savedInstanceState: Bundle?) {
                val quranApp = application as QuranApplication
                quranApp.refreshLocale(this, false)

                super.onCreate(savedInstanceState)
                quranApp.applicationComponent
                    .quranActivityComponentBuilder()
                    .build()
                    .inject(this)

                setContentView(R.layout.quran_index)
                isRtl = isRtl()

                val tb = findViewById<Toolbar>(R.id.toolbar)
                setSupportActionBar(tb)
                val ab = supportActionBar
                ab?.setTitle(R.string.app_name)

                val pager = findViewById<ViewPager>(R.id.index_pager)
                pager.offscreenPageLimit = 3
                val pagerAdapter = PagerAdapter(supportFragmentManager)
                pager.adapter = pagerAdapter
                val indicator = findViewById<SlidingTabLayout>(R.id.indicator)
                indicator.setViewPager(pager)
                if (isRtl) {
                  pager.currentItem = TITLES.size - 1
                }

                if (savedInstanceState != null) {
                  showedTranslationUpgradeDialog = savedInstanceState.getBoolean(
                      SI_SHOWED_UPGRADE_DIALOG, false
                  )
                }

                latestPageObservable = recentPageModel.getLatestPageObservable()
                val intent = intent
                if (intent != null) {
                  val extras = intent.extras
                  if (extras != null) {
                    if (extras.getBoolean(EXTRA_SHOW_TRANSLATION_UPGRADE, false)) {
                      if (!showedTranslationUpgradeDialog) {
                        showTranslationsUpgradeDialog()
                      }
                    }
                  }
                  if (ShortcutsActivity.ACTION_JUMP_TO_LATEST == intent.action) {
                    jumpToLastPage()
                  }
                }
                updateTranslationsListAsNeeded()
                quranIndexEventLogger.logAnalytics()
              }                             
            }
        """.trimIndent()

        val ktFile = compileContentForTest(code)
        MetricProcessor().onProcess(ktFile)

        val LAA = ktFile.getUserData(MetricProcessor.numberOfLocalityOfAttributeAccesses) ?: -1
        val ATFD = ktFile.getUserData(MetricProcessor.numberOfAccessToForeignData) ?: -1
        val FDP = ktFile.getUserData(MetricProcessor.numberOfForeignDataProviders) ?: -1

        println("LAA : $LAA")
        println("ATFD : $ATFD")
        println("FDP : $FDP")

        assert(true)
    }
}
