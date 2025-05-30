package com.demo.bleclient.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.demo.bleclient.R
import com.demo.bleclient.base.BaseActivity
import com.demo.bleclient.databinding.ActivitySplashBinding
import com.demo.bleclient.utils.getStr
import com.google.android.material.dialog.MaterialAlertDialogBuilder


@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {



    override fun initView() {
        binding.btnGoToNextScreen.setOnClickListener {
            checkPermissionsForBluetoothAndLocation {
                nextScreenNavigation()
            }
        }
    }

    private val enableBluetoothLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            nextScreenNavigation()
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
            nextScreenNavigation()
        }
    }

    override fun onBackPress() {
        finish()
    }

    private fun nextScreenNavigation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (!isEnabled) {
            // Prompt user to enable location in settings
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }else{
            startActivity(Intent(this, ScanActivity::class.java))
            finish()
        }
    }

    private val permissionBtCheck = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val deniedPermissions = permissions.filterValues { !it }.keys.toTypedArray()
        if (deniedPermissions.isEmpty()) {
            nextScreenNavigation()
        } else {
            handlePermissionDenied(deniedPermissions)
        }
    }

    private fun checkPermissionsForBluetoothAndLocation(onAllGranted: () -> Unit) {
        val permissionsToRequest = mutableListOf<String>()

        if (ActivityCompat.checkSelfPermission(this@SplashActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)

        if (SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this@SplashActivity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)

            if (ActivityCompat.checkSelfPermission(this@SplashActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
        }

        if (permissionsToRequest.isEmpty()) {
            onAllGranted()
        } else {
            showPermissionDialog(permissions = permissionsToRequest.toTypedArray())
        }
    }

    private fun handlePermissionDenied(permissions: Array<String>) {
        val shouldShowRationale = permissions.any {
            ActivityCompat.shouldShowRequestPermissionRationale(this, it)
        }

        if (shouldShowRationale) {
            showPermissionDialog(permissions = permissions)
        } else {
            showSettingsDialog()
        }
    }

    private fun showPermissionDialog(
        permissions: Array<String>
    ) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getStr(R.string.permission_required))
            .setMessage(getStr(R.string.bt_and_location_permission_description))
            .setPositiveButton(getStr(R.string.btn_grant)) { _, _ ->
                permissionBtCheck.launch(permissions)
            }
            .setNegativeButton(getStr(R.string.btn_cancel), null)
            .show()
    }

    private fun showSettingsDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getStr(R.string.permission_denied))
            .setMessage(getStr(R.string.permission_denied_description_multiple_time))
            .setPositiveButton(getStr(R.string.btn_open_setting)) { _, _ ->
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                })
            }
            .setNegativeButton(getStr(R.string.btn_cancel), null)
            .show()
    }
}
