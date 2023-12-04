package com.ibm.internship.beabee.ui.dashboard.myevents

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class EventsAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val fragmentList: MutableList<Fragment> = ArrayList()
    private val fragmentTitleList: MutableList<Int> = ArrayList()

    fun addFragment(fragment: Fragment, stringResource: Int) {
        fragmentList.add(fragment)
        fragmentTitleList.add(stringResource)
    }

    fun getTabTitle(position : Int) = fragmentTitleList[position]

    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int) = fragmentList[position]
}