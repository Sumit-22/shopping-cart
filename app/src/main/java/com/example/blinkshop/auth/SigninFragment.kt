package com.example.blinkshop.auth

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkshop.Constants
import com.example.blinkshop.R
import com.example.blinkshop.Utils
import com.example.blinkshop.adapters.ImageAdapter
import com.example.blinkshop.databinding.FragmentSigninBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

//5.4.1
class SigninFragment : Fragment() {
    private lateinit var binding : FragmentSigninBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSigninBinding.inflate(layoutInflater)
        setAnim()
        setStatusBarColor()
        getUserNumber()
        onContinueButtonClick()
        // Inflate the layout for this fragment
        return binding.root


    }

    private fun setAnim() {
        val recyclerView1 =binding.recyclerView1
        val recyclerView2 =binding.recyclerView2
        val recyclerView3 =binding.recyclerView3
        val recyclerView4 =binding.recyclerView4
        val imageList=Constants.allProductsCategoryIcon.toList()
        val oneFourthIndex = imageList.size / 4
        val twoFourthIndex = (2 * imageList.size) / 4
        val threeFourthIndex = (3 * imageList.size) / 4
        val imageList1 = imageList // Full list for the first RecyclerView
        val imageList2 = imageList.subList(oneFourthIndex, imageList.size) + imageList.subList(0, oneFourthIndex)
        val imageList3 = imageList.subList(twoFourthIndex, imageList.size) + imageList.subList(0, twoFourthIndex)
        val imageList4 = imageList.subList(threeFourthIndex, imageList.size) + imageList.subList(0, threeFourthIndex)

        val adapter1 = ImageAdapter(imageList1)
        val adapter2 = ImageAdapter(imageList2)
        val adapter3 = ImageAdapter(imageList3)
        val adapter4 = ImageAdapter(imageList4)
        recyclerView1.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        recyclerView2.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        recyclerView3.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        recyclerView4.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

        recyclerView1.adapter = adapter1
        recyclerView2.adapter = adapter2
        recyclerView3.adapter = adapter3
        recyclerView4.adapter = adapter4
//        fun setupInfiniteScroll(recyclerView: RecyclerView) {
//            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//            val handler = Handler(Looper.getMainLooper())
//            val runnable = object : Runnable {
//                override fun run() {
//                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
//                    val totalItemCount = layoutManager.itemCount
//                    if (firstVisibleItemPosition == totalItemCount - 1) {
//                        recyclerView1.smoothScrollToPosition(0)
//                    } else {
//                        recyclerView1.smoothScrollBy(10, 0)
//                    }
//                    handler.postDelayed(this, 10) // Scroll delay
//                }
//            }
//            handler.post(runnable)
//        }
//        setupInfiniteScroll(recyclerView1)
//        setupInfiniteScroll(recyclerView2)
//        setupInfiniteScroll(recyclerView3)

// Start infinite scrolling
        CoroutineScope(Dispatchers.Main).launch {
            val scroll1 = async { setupInfiniteScroll(recyclerView1) }
            val scroll2 = async { setupInfiniteScroll(recyclerView2) }
            val scroll3 = async { setupInfiniteScroll(recyclerView3) }
            val scroll4 = async { setupInfiniteScroll(recyclerView4) }

            // Optionally, wait for all coroutines to finish (not necessary here)
            scroll1.await()
            scroll2.await()
            scroll3.await()
            scroll4.await()
        }
    }

    private fun setupInfiniteScroll(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val handler = Handler(Looper.getMainLooper())

        val runnable = object : Runnable {
            override fun run() {
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                // Infinite scroll logic (when reaching the last item, scroll back to the first)
                if (firstVisibleItemPosition == totalItemCount - 1) {
                    recyclerView.smoothScrollToPosition(0)
                } else {
                    recyclerView.smoothScrollBy(15, 0) // Scroll by a small amount
                }
                handler.postDelayed(this, 20) // Scroll delay
            }
        }

        handler.post(runnable) // Start the scrolling
    }

    private fun onContinueButtonClick() {
        binding.btnContinue.setOnClickListener{
            val number  = binding.etUserNumber.text.toString()
            if (number.isEmpty() || number.length !=10 ){
                Utils.showToast(requireContext(),"Please enter valid phone number")
            }
            else{
                val bundle=Bundle()
                bundle.putString("number",number)
                findNavController().navigate(R.id.action_signinFragment_to_OTPFragment,bundle)

            }
        }

    }

    private fun getUserNumber() {
        //addtextchangedinterface
        binding.etUserNumber.addTextChangedListener (object:TextWatcher {
            override fun beforeTextChanged(number: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
                val len = number?.length
                if (len == 10) {
                    binding.btnContinue.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.correctbtncolor
                        )
                    )
                } else {
                    binding.btnContinue.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.wrongbtncolor
                        )
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
      )

    }
    /* In Kotlin,1> this refers to the current instance of the class or context where it is used.
    2> this@MainActivity:-When you have nested scopes (such as inner classes or lambdas), you can use this@ClassName to explicitly refer to the outer class or context.
    It’s useful when there are name conflicts (e.g., if you have a local variable with the same name as a class property).
    3>requireContext():It is a method available in Android Fragments.It returns the non-null context associated with the fragment.
    If the fragment is not attached to a context (e.g., during certain lifecycle phases), it throws an IllegalStateException.Use it when you need a context within a fragment.
     */

    private fun setStatusBarColor(){
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor = statusBarColors
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }


}