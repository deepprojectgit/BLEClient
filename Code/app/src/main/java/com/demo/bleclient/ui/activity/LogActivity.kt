package com.demo.bleclient.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.bleclient.R
import com.demo.bleclient.base.BaseActivity
import com.demo.bleclient.databinding.ActivityLogBinding
import com.demo.bleclient.databinding.ActivityScanBinding
import com.demo.bleclient.ui.adaptor.BleDeviceAdaptor
import com.demo.bleclient.ui.adaptor.LogListAdaptor
import com.demo.bleclient.utils.KEY_ADDRESS
import com.demo.bleclient.viewmodel.LogViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogActivity : BaseActivity<ActivityLogBinding>(ActivityLogBinding::inflate) {

    private val  logListAdaptor: LogListAdaptor by lazy {
        LogListAdaptor()
    }

    private val logViewModel : LogViewModel by viewModels()
    lateinit var strAddress :String
    override fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) // Show back arrow
            setDisplayShowHomeEnabled(true)
        }

        strAddress = intent.getStringExtra(KEY_ADDRESS)?:""

        binding.rvDevices.apply {
            this.adapter = logListAdaptor
            this.layoutManager = LinearLayoutManager(this@LogActivity)
            this.itemAnimator = DefaultItemAnimator()
        }

        setObserver()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // or simply finish()
        return true
    }

    override fun onBackPress() {
       finish()
    }

    private fun setObserver(){
        logViewModel.mDoorLiveData(strAddress).observe(this@LogActivity) {
            logListAdaptor.updateList(it)
            if(it.isNotEmpty()){
                binding.rvDevices.visibility = View.VISIBLE
                binding.tvNoDataFound.visibility = View.GONE
            }else{
                binding.rvDevices.visibility = View.GONE
                binding.tvNoDataFound.visibility = View.VISIBLE
            }
            binding.rvDevices.scrollToPosition(it.size - 1)
        }
    }

}