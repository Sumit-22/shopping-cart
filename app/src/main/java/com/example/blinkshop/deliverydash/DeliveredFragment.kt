import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.blinkshop.R
import com.example.blinkshop.databinding.FragmentDeliveredBinding

class DeliveredFragment : Fragment() {
    private lateinit var binding:FragmentDeliveredBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeliveredBinding.inflate(layoutInflater)
        return binding.root
    }
}
