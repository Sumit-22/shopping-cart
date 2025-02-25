import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.blinkshop.R
import com.example.blinkshop.databinding.FragmentAvailableBinding

class AvailableFragment : Fragment() {
    private lateinit var binding:FragmentAvailableBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAvailableBinding.inflate(layoutInflater)
        return binding.root
    }
}
