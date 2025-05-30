package com.demo.bleclient.ui.adaptor

import android.content.Context
import android.util.Log
import android.view.View
import com.demo.bleclient.R
import com.demo.bleclient.base.BaseViewAdapter
import com.demo.bleclient.data.model.LogEntity
import com.demo.bleclient.databinding.ItemDevicesBinding
import com.demo.bleclient.databinding.ItemLogListBinding


class LogListAdaptor : BaseViewAdapter<LogEntity, ItemLogListBinding>({ inflater, parent, attach ->
    ItemLogListBinding.inflate(
        inflater,
        parent,
        attach
    )
},
    compareItems = { old, new -> old.id == new.id },
    compareContents = { old, new -> old == new }
) {
    override fun onBind(binding: ItemLogListBinding, item: LogEntity, position: Int) {

        binding.tvStatus.text = item.logType
        binding.tvTime.text = item.logTime
        binding.tvDate.text = item.logDate

        if (position == 0) {
            binding.tvDate.visibility = View.VISIBLE
        } else {
            val previousItemDate = getList()[position - 1].logDate
            val currentItemDate = getList()[position].logDate
            if(previousItemDate != currentItemDate){
                binding.tvDate.visibility = View.VISIBLE
            }else{
                binding.tvDate.visibility = View.GONE
            }
        }
    }

}