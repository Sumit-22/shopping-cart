package com.example.blinkshop.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.blinkshop.R
import com.example.blinkshop.Utils
import com.example.blinkshop.activity.AuthMainActivity
import com.example.blinkshop.databinding.AddressBookLayoutBinding
import com.example.blinkshop.databinding.AddressLayoutBinding
import com.example.blinkshop.databinding.FragmentProfileBinding
import com.example.blinkshop.viewmodels.userViewModel


class ProfileFragment : Fragment() {
   private lateinit var binding: FragmentProfileBinding

   private val viewModel:userViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =FragmentProfileBinding.inflate(layoutInflater)
        setStatusBarColor()
        onBackButtonClicked()
        onOrdersClicked()
        onAddressBookClicked()
        onLogOutClicked()
        return binding.root
    }

    private fun onLogOutClicked() {
        binding.llLogOut.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
                val alertDialog =builder.create()
                builder.setTitle("Log out")
                .setMessage("Do you want to log out ?")
                .setPositiveButton("Yes"){_,_->
                    viewModel.logOutUser()
                    startActivity(Intent(requireContext(),AuthMainActivity::class.java))
                    requireActivity().finish()
                }
                .setNegativeButton("No"){_,_->
                    alertDialog.dismiss()
                }
                    .show()
                    .setCancelable(false)
        }
    }

    private fun onAddressBookClicked() {
        binding.llAddressBook.setOnClickListener {
            val addressBookLayoutBinding = AddressBookLayoutBinding.inflate(LayoutInflater.from(requireContext()))
            viewModel.getUserAddress {address->
                addressBookLayoutBinding.etAddress.setText(address.toString())


            }
            val alertDialog = AlertDialog.Builder(requireContext())
                .setView(addressBookLayoutBinding.root)
                .create()
            alertDialog.show()
            addressBookLayoutBinding.btnEdit.setOnClickListener {
                addressBookLayoutBinding.etAddress.isEnabled=true

            }
            addressBookLayoutBinding.btnSave.setOnClickListener {
                viewModel.saveAddress(addressBookLayoutBinding.etAddress.text.toString())
                alertDialog.dismiss()
                Utils.showToast(requireContext(),"Address updated..")
            }

        }
    }

    private fun onOrdersClicked() {
        binding.llOrders.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
        }
    }

    private fun onBackButtonClicked() {
        binding.tbProfileFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
        }
    }
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