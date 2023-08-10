package org.example.detekt

import io.github.detekt.test.utils.compileContentForTest
import org.example.detekt.util.Edge
import org.example.detekt.util.Graph
import org.junit.jupiter.api.Test
import java.text.DecimalFormat
import java.util.*

class GodClassTest {
    @Test
    fun `should expect god class`(){
        val code = """
            package org.informatika.if5250rajinapps.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import org.informatika.if5250rajinapps.R
import org.informatika.if5250rajinapps.activity.MainActivity
import org.informatika.if5250rajinapps.databinding.FragmentHomeBinding
import org.informatika.if5250rajinapps.util.formattedDateOnly
import org.informatika.if5250rajinapps.util.formattedTimeOnly
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var navController: NavController
    private lateinit var mAuth: FirebaseAuth
    private val binding get() = _binding!!

    var viewModel : HomeViewModel? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        mAuth = FirebaseAuth.getInstance()

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        binding.tvTimeCreate.text = Date().formattedDateOnly
        binding.tvCheckIn.text = "Masuk : -"
        binding.tvCheckOut.text = "Pulang : -"
        binding.tvDurasi.text = "Durasi Kerja : -"

        viewModel!!.staff.observe(viewLifecycleOwner) {
            it.let {
                binding.tvNama.text = it?.nama
                binding.tvNoInduk.text = it?.noInduk

            }
        }

        viewModel!!.unitkerja.observe(viewLifecycleOwner) {
            it.let {
                binding.tvOrganisasi.text = it?.nama
            }
        }

        viewModel!!.presensi.observe(viewLifecycleOwner) {
            
        }

        return root
        }
    
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            this.navController = Navigation.findNavController(view)
        }
    
        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    
        override fun onResume() {
            viewModel.let {
                it?.fetchPresence()
            }
            super.onResume()
        }
    
        override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
            inflater.inflate(R.menu.home_action_bar_menu, menu)
        }
    
        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            return when(item.itemId){
                R.id.action_logout -> {
                    actionSignOut()
                    true
                }
                else ->
                    super.onOptionsItemSelected(item)
            }
        }
    
        private fun actionSignOut() {
            mAuth.signOut()
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
            activity?.finish()
        }
    }
        """

        val codeOld = """
            class Test {
    var x = 0
    var y = 0
    
    fun methodA() {
        x = 1
    }
    
    fun methodB() {
        x = 2
    }
    
    fun methodC() {
        x = 3
        y = 1
    }
    
    fun methodD() {
        y = 2
    }
    
    fun methodE(val a: Int, val b: Int) {
        
    }
}

        """

        val ktFile = compileContentForTest(code)
        MetricProcessor().onProcess(ktFile)

        val ATFD = ktFile.getUserData(MetricProcessor.numberOfAccessToForeignData)
        val WMC = ktFile.getUserData(MetricProcessor.numberOfWeightedMethodCount)
        val TCC = ktFile.getUserData(MetricProcessor.numberOfTightClassCohesion)

        val dec = DecimalFormat("#.##")

        println("ATFD : $ATFD")
        println("WMC : $WMC")
        println("TCC : $TCC")
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
