package com.android.budgetbuddy.ui.tips

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.adapter.TipsAdapter
import com.android.budgetbuddy.data.model.Tips
import com.android.budgetbuddy.databinding.FragmentTipsBinding


class TipsFragment:Fragment() {

    private lateinit var binding: FragmentTipsBinding
    private lateinit var tipsAdapter: TipsAdapter
    private val list = ArrayList<Tips>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTipsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!::tipsAdapter.isInitialized) {
            tipsAdapter = TipsAdapter(list)
            binding.rvTips.apply {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = tipsAdapter
            }
        }


        list.clear()
        list.addAll(getListFlowers())
        tipsAdapter.notifyDataSetChanged()
    }

    @SuppressLint("Recycle")
    private fun getListFlowers():ArrayList<Tips> {
        val dataName = resources.getStringArray(R.array.data_name)
        val dataDescription = resources.getStringArray(R.array.data_description)
        val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
        val listTips = ArrayList<Tips>()
        for (i in dataName.indices) {
            val hero = Tips(dataName[i], dataDescription[i], dataPhoto.getResourceId(i, -1))
            listTips.add(hero)
        }
        return listTips
    }

}