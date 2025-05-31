package com.demo.bleclient.ui.adaptor

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.demo.bleclient.base.BaseViewAdapter
import com.demo.bleclient.databinding.ItemDevicesBinding


class BleDeviceAdaptor (val positiveButtonClick : (bleDevice:BluetoothDevice) -> Unit) : BaseViewAdapter<BluetoothDevice, ItemDevicesBinding>({ inflater, parent, attach ->
    ItemDevicesBinding.inflate(
        inflater,
        parent,
        attach
    )
},
    compareItems = { old, new -> old.address == new.address },
    compareContents = { old, new -> old == new })
{

    @SuppressLint("MissingPermission")
    override fun onBind(binding: ItemDevicesBinding, item: BluetoothDevice, position: Int) {
        binding.tvDeviceName.text = if(item.name.isNullOrEmpty()) "--" else item.name
        binding.tvDeviceAddress.text = item.address

        binding.root.setOnClickListener {
            positiveButtonClick.invoke(item)
        }
    }
}