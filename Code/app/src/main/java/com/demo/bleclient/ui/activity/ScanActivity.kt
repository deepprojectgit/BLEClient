package com.demo.bleclient.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.bleclient.R
import com.demo.bleclient.base.BaseActivity
import com.demo.bleclient.ble.BleClient
import com.demo.bleclient.receiver.BluetoothReceiver
import com.demo.bleclient.databinding.ActivityScanBinding
import com.demo.bleclient.ui.adaptor.BleDeviceAdaptor
import com.demo.bleclient.utils.KEY_ADDRESS
import com.demo.bleclient.utils.StatusBLEConnection
import com.demo.bleclient.utils.UtilsFunc
import com.demo.bleclient.utils.lifeCycleLaunch
import com.demo.bleclient.viewmodel.LogViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScanActivity : BaseActivity<ActivityScanBinding>(ActivityScanBinding::inflate) {
    var menuItem: MenuItem ?= null
    var menu: Menu?= null

    private val logViewModel : LogViewModel by viewModels()
    var isScanProcessStart: Boolean = false

    private val  bleDeviceAdaptor: BleDeviceAdaptor by lazy {
        BleDeviceAdaptor { bleDevice->
            if(!isScanProcessStart){
                logViewModel.getBLEClient().disconnect()
                logViewModel.getBLEClient().connectToDevice(bleDevice)
                val intent = Intent(this@ScanActivity,LogActivity::class.java)
                intent.putExtra(KEY_ADDRESS,bleDevice.address)
                startActivity(intent)
            }else{
                Toast.makeText(this@ScanActivity,"Scanning process working please wait",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun initView() {
        setSupportActionBar(binding.toolbar)
        binding.rvDevices.apply {
            this.adapter = bleDeviceAdaptor
            this.layoutManager = LinearLayoutManager(this@ScanActivity)
            this.itemAnimator = DefaultItemAnimator()
        }
        if(bleDeviceAdaptor.getList().size>0){
            binding.rvDevices.visibility = View.VISIBLE
            binding.tvNoDataFound.visibility = View.GONE
        }else{
            binding.rvDevices.visibility = View.GONE
            binding.tvNoDataFound.visibility = View.VISIBLE
        }
        setupObservers()
        //UtilsFunc.startBleForegroundService(this@ScanActivity)
    }

    override fun onBackPress() {
       finish()
    }

    private fun setupObservers() {
        logViewModel.getBLEClient().onCheckBluetooth = { device, status,logEntity ->
            status?.let {
                when(it){
                    StatusBLEConnection.IS_DEVICE_SCANNING_START->{
                        isScanProcessStart = true
                        binding.pbLoader.visibility = View.VISIBLE
                        menuItem?.icon?.setTint(ContextCompat.getColor(this, R.color.gray))
                    }
                    StatusBLEConnection.IS_DEVICE_SCANNING_STOP->{
                        isScanProcessStart = false
                        binding.pbLoader.visibility = View.GONE
                        menuItem?.icon?.setTint(ContextCompat.getColor(this, R.color.green))
                    }
                    StatusBLEConnection.IS_DEVICE_CONNECTED_COMPLETED->{

                    }
                    StatusBLEConnection.IS_DEVICE_CONNECTED_FAIL->{

                    }
                    StatusBLEConnection.IS_DEVICE_GET_SUCCESS->{
                        menuItem?.icon?.setTint(ContextCompat.getColor(this, R.color.gray))
                        device?.let { device->
                            val count =  bleDeviceAdaptor.getList().count { item-> item.address == device.address}
                            if(count==0){
                                bleDeviceAdaptor.addItem(device)
                            }
                        }
                        if(bleDeviceAdaptor.getList().size>0){
                            binding.rvDevices.visibility =View.VISIBLE
                            binding.tvNoDataFound.visibility =View.GONE
                        }else{
                            binding.rvDevices.visibility =View.GONE
                            binding.tvNoDataFound.visibility =View.VISIBLE
                        }
                    }
                    StatusBLEConnection.IS_DEVICE_GET_FAIL->{
                        menuItem?.icon?.setTint(ContextCompat.getColor(this, R.color.gray))
                    }
                }
            }
        }
    }

    private fun updatePowerIcon(item: MenuItem?) {
        if (isScanProcessStart) {
            item?.icon?.setTint(ContextCompat.getColor(this, R.color.gray))
        } else {
            item?.icon?.setTint(ContextCompat.getColor(this, R.color.green))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)
        this.menu = menu
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val powerItem = menu.findItem(R.id.action_power)
        updatePowerIcon(powerItem)
        return super.onPrepareOptionsMenu(menu)
    }

    @SuppressLint("MissingPermission")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_power) {
            menuItem = item
            enableBluetooth()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private val enableBluetoothLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            menuItem  = menu?.findItem(R.id.action_power)
            if(!isScanProcessStart){
                updatePowerIcon(menuItem)
            }else{
                Toast.makeText(this@ScanActivity,"Scanning process working please wait",Toast.LENGTH_SHORT).show()
            }
        } else {
            enableBluetooth()
        }
    }

    private fun enableBluetooth(){
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        if (!bluetoothAdapter.isEnabled && bluetoothAdapter != null) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBtIntent)
        }else{
            if(bluetoothAdapter != null){
                if(!isScanProcessStart){
                    logViewModel.getBLEClient().startScan()
                }else{
                    Toast.makeText(this@ScanActivity,"Scanning process working please wait",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        logViewModel.getBLEClient().stopScan()
        isScanProcessStart = false
    }

    override fun onDestroy() {
        super.onDestroy()
        logViewModel.getBLEClient().disconnect()
    }

}